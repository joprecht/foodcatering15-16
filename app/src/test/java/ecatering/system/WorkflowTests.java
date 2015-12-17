package ecatering.system;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.salespointframework.order.Cart;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.accountancy.Debit;
import org.tudresden.ecatering.model.accountancy.MealOrder;
import org.tudresden.ecatering.model.accountancy.Transfer;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.customer.Customer;
import org.tudresden.ecatering.model.customer.CustomerManager;
import org.tudresden.ecatering.model.kitchen.DailyMenu;
import org.tudresden.ecatering.model.kitchen.Day;
import org.tudresden.ecatering.model.kitchen.Helping;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.kitchen.Menu;
import org.tudresden.ecatering.model.kitchen.MenuItem;
import org.tudresden.ecatering.model.stock.StockManager;
import org.tudresden.ecatering.model.stock.StockReport;

import ecatering.AbstractIntegrationTests;

public class WorkflowTests extends AbstractIntegrationTests {

	
		@Autowired BusinessManager businessManager;
		@Autowired UserAccountManager userAccountManager;
		@Autowired CustomerManager customerManager;
		@Autowired StockManager stockManager;
		@Autowired KitchenManager kitchenManager;
		@Autowired OrderManager<MealOrder> mealOrderManager; 

		
		@Test
		public void workflowTests() {
			
			//initialisiert wird mit Admin Account als Buchhalter
			userAccountManager.save(userAccountManager.create("buchhalter", "123", Role.of("ROLE_ACCOUNTING"),Role.of("ROLE_ADMIN")));
			
			
			//Admin/Buchhalter kann nun über Controller(role_admin) Mitarbeiter anlegen
			//er erstellt einen Lager- und KüchenAccount
			userAccountManager.save(userAccountManager.create("kueche", "123", Role.of("ROLE_KITCHEN")));
			userAccountManager.save(userAccountManager.create("lagerist", "123", Role.of("ROLE_STOCK")));
			
			
			//Admin/Buchhalter kann ebenfalls über Controller(role_admin) Firmen eintragen, welche das Catering System nutzen
			//er erstellt ein Grossunternehmen und eine Einrichtung für Kinderbetreuung
			
			businessManager.saveBusiness(businessManager.createCompanyBusiness("Stahlwerk Sonnenschein", 
																	new Address("Max",
																				"Mustermann",
																				"Industriestrasse",
																				"21a",
																				"01234",
																				"Musterstadt",
																				"Musterland"), 
																				"kundenCode-1234"));
			
			businessManager.saveBusiness(businessManager.createChildcareBusiness("Kita Marienhof", 
																		new Address("Maria",
																					"Musterfrau",
																					"Marienstrasse",
																					"12",
																					"01234",
																					"Musterstadt",
																					"Musterland"), 
																					"elternCode-1234",
																					"lehrerCode-1234"));

	//noch nicht registrierte Kunden erstellen nun einen Account über Controller(registration)
	//Bei Registration werden neben den üblichen Angaben auch ein Code benötigt
			
		//Kunde Großunternehmen
			UserAccount ua1 = userAccountManager.create("stahlarbeiter", "123", Role.of("ROLE_CUSTOMER"));
										ua1.setFirstname("Torsten");
										ua1.setLastname("Mueller");
										ua1.setEmail("torsten@mueller.de");

			//wenn auch dieser Code gültig ist, wird der Kunde erfolgreich registriert							
			assertTrue(businessManager.findBusinessByCode("kundenCode-1234").isPresent());
			
				userAccountManager.save(ua1);
				Customer firmenkunde = customerManager.saveCustomer(customerManager.createCustomer(ua1, "kundenCode-1234"));

				
		//Kunde Kinderbetreuung(Kind)
				UserAccount ua2 = userAccountManager.create("kind", "123", Role.of("ROLE_CUSTOMER"));
				ua1.setFirstname("Fabian");
				ua1.setLastname("Schaller");
				ua1.setEmail("fabian@schaller.de");

			assertTrue(businessManager.findBusinessByCode("elternCode-1234").isPresent());

				userAccountManager.save(ua2);
				Customer kind = customerManager.saveCustomer(customerManager.createCustomer(ua2, "elternCode-1234"));
				
			
	
		//Kunde Kinderbetreuung(Lehrer)
				UserAccount ua3 = userAccountManager.create("lehrer", "123", Role.of("ROLE_CUSTOMER"));
				ua1.setFirstname("Maria");
				ua1.setLastname("Musterfrau");
				ua1.setEmail("maria@musterfrau.de");

			assertTrue(businessManager.findBusinessByCode("lehrerCode-1234").isPresent());

				userAccountManager.save(ua3);
				Customer lehrer = customerManager.saveCustomer(customerManager.createCustomer(ua3, "elternCode-1234"));
		
				
	//im Lager trägt der Lagerist über Controller(role_stock) alle Lebensmittel ein, welche über einen Zulieferer
	//beschafft werden können	
				
				//kurzer check - im DataInitializer wurden schon 5 Lebensmittel hinzugefügt
				//Lager besitzt zwei Lagergüter
				assertThat(stockManager.findAllGroceries(), is(iterableWithSize(5)));
				assertThat(stockManager.findAllStockItems(), is(iterableWithSize(2)));

				
	//in der Küche(Controller(role_kitchen)) kann nun der Koch Rezepte erstellen, basierend auf den verfügbaren Lebensmittel
		
				//kurzer check - im DataInitializer wurden schon 3 Rezepte bestehend aus den 5 Lebensmitteln erstellt
				assertThat(kitchenManager.findAllRecipes(), is(iterableWithSize(3)));
				
				
				
	//der Buchhalter (Controller(role_accounting) schaut sich die Rezepte an, und entscheidet welches eine feste
	//Mahlzeit werden soll. Dabei gibt er auch einen Faktor für Gewinn an. Es wird zudem auch ein Speiseplan erstellt.
				
				//kurzer check - im DataInitializer wurden aus allen Rezepten schon Mahlzeiten gebaut
				assertThat(kitchenManager.findAllMeals(), is(iterableWithSize(3)));			
				assertThat(kitchenManager.findAllMenus(), is(iterableWithSize(0)));
				
				List<MenuItem> mondayMeals = new ArrayList<MenuItem>();
				mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.MONDAY));
				mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.MONDAY));
				mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.MONDAY));

				List<MenuItem> tuesdayMeals = new ArrayList<MenuItem>();
				tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.TUESDAY));
				tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.TUESDAY));
				tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.TUESDAY));
				
				List<MenuItem> wednesdayMeals = new ArrayList<MenuItem>();
				wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.WEDNESDAY));
				wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.WEDNESDAY));
				wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.WEDNESDAY));

				List<MenuItem> thursdayMeals = new ArrayList<MenuItem>();
				thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.THURSDAY));
				thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.THURSDAY));
				thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.THURSDAY));
				
				List<MenuItem> fridayMeals = new ArrayList<MenuItem>();
				fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR,Day.FRIDAY));
				fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR,Day.FRIDAY));
				fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR,Day.FRIDAY));
					
				List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
				dailyMenus.add(kitchenManager.createDailyMenu(mondayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(tuesdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(wednesdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(thursdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(fridayMeals));

				kitchenManager.saveMenu(kitchenManager.createMenu(53, dailyMenus));
				
				
				//Es wird auch noch ein Kinder Speiseplan für diese Woche angeboten
				
				
			    mondayMeals = new ArrayList<MenuItem>();
				mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.MONDAY));
				mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.MONDAY));
				mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.MONDAY));

				tuesdayMeals = new ArrayList<MenuItem>();
				tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.TUESDAY));
				tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.TUESDAY));
				tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.TUESDAY));
				
				wednesdayMeals = new ArrayList<MenuItem>();
				wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.WEDNESDAY));
				wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.WEDNESDAY));
				wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.WEDNESDAY));

				thursdayMeals = new ArrayList<MenuItem>();
				thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.THURSDAY));
				thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.THURSDAY));
				thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.THURSDAY));
				
				fridayMeals = new ArrayList<MenuItem>();
				fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.SMALL,Day.FRIDAY));
				fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.SMALL,Day.FRIDAY));
				fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.SMALL,Day.FRIDAY));
					
				dailyMenus = new ArrayList<DailyMenu>();
				dailyMenus.add(kitchenManager.createDailyMenu(mondayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(tuesdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(wednesdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(thursdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(fridayMeals));

				kitchenManager.saveMenu(kitchenManager.createMenu(53, dailyMenus));
				
				
//##########   Ablaufplan für 3 Wochen ########//				
				
	//1.Woche : 51KW / 14.12.2015 - 20.12.2015
	//Ein Speiseplan wird dem Kunden gezeigt, woraufhin dieser für die übernächste Woche bestellen kann
		
				LocalDate testDate = LocalDate.now();
				
				//Speiseplan für übernächste Woche
				assertThat(kitchenManager.findMenusByDate(testDate.plusWeeks(2)),is(iterableWithSize(2)));
				
				Menu speisePlanNormal=null;
				Menu speisePlanKlein=null;
				Iterable<Menu> menus = kitchenManager.findMenusByDate(testDate.plusWeeks(2));
				Iterator<Menu> iter = menus.iterator();
			
				while(iter.hasNext())
				{
					Menu menu = iter.next();
					if(menu.getHelping().equals(Helping.REGULAR))
					{
						speisePlanNormal = menu;
					}
					else
					{
						speisePlanKlein = menu;
					}
				}

				
		//Bestellungen						
			//Kunde Großunternehmen bestellt per Überweisung (ua1)
				Cart cart = new Cart();

				//montag 1 mal normal
				cart.addOrUpdateItem(speisePlanNormal.getDailyMenus().get(0).getDailyMeals().get(0), Quantity.of(1.0));
				//dienstag 1 mal spezial
				cart.addOrUpdateItem(speisePlanNormal.getDailyMenus().get(1).getDailyMeals().get(1), Quantity.of(1.0));
				//freitag 5 mal diät
				cart.addOrUpdateItem(speisePlanNormal.getDailyMenus().get(4).getDailyMeals().get(2), Quantity.of(5.0));

				//Rechnungsadresse
				Address invoiceAddress = new Address(ua1.getFirstname(),ua1.getLastname(),"Musterstrasse","123","01307","Dresden","Deutschland");
				
				MealOrder order = new MealOrder(firmenkunde,Transfer.TRANSFER,invoiceAddress);
				cart.addItemsTo(order);
				mealOrderManager.save(order);
				
				
			//Eltern/Kind bestellt per Überweisung (ua2)
				cart = new Cart();
				
				//montag 1 mal normal
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(0).getDailyMeals().get(0), Quantity.of(1.0));
				//dienstag 1 mal spezial
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(1).getDailyMeals().get(1), Quantity.of(1.0));
				//mittwoch 1 mal normal 
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(2).getDailyMeals().get(0), Quantity.of(1.0));
				//donnerstag 1 mal normal
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(3).getDailyMeals().get(0), Quantity.of(1.0));
				//freitag 1 mal diät
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(4).getDailyMeals().get(2), Quantity.of(1.0));
				
				//Rechnungsadresse
				invoiceAddress = new Address(ua2.getFirstname(),ua2.getLastname(),"Musterstrasse","541","01307","Dresden","Deutschland");
				
				order = new MealOrder(kind,Transfer.TRANSFER,invoiceAddress);
				cart.addItemsTo(order);
				mealOrderManager.save(order);
				
				
			//Lehrer bestellt per Lastschrift (ua3)
				cart = new Cart();
				
				//montag 10 mal normal, 3 mal spezial, 1 mal diet
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(0).getDailyMeals().get(0), Quantity.of(10.0));
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(0).getDailyMeals().get(1), Quantity.of(3.0));
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(0).getDailyMeals().get(2), Quantity.of(1.0));
				//dienstag 10 mal normal, 3 mal spezial, 1 mal diet
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(1).getDailyMeals().get(0), Quantity.of(10.0));
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(1).getDailyMeals().get(1), Quantity.of(3.0));
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(1).getDailyMeals().get(2), Quantity.of(1.0));
				//mittwoch 10 mal normal, 3 mal spezial, 1 mal diet 
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(2).getDailyMeals().get(0), Quantity.of(10.0));
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(2).getDailyMeals().get(1), Quantity.of(3.0));
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(2).getDailyMeals().get(2), Quantity.of(1.0));
				//donnerstag 10 mal normal, 3 mal spezial, 1 mal diet
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(3).getDailyMeals().get(0), Quantity.of(10.0));
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(3).getDailyMeals().get(1), Quantity.of(3.0));
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(3).getDailyMeals().get(2), Quantity.of(1.0));
				//freitag 10 mal normal, 3 mal spezial, 1 mal diet
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(4).getDailyMeals().get(0), Quantity.of(10.0));
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(4).getDailyMeals().get(1), Quantity.of(3.0));
				cart.addOrUpdateItem(speisePlanKlein.getDailyMenus().get(4).getDailyMeals().get(2), Quantity.of(1.0));
				
				//Rechnungsadresse
				invoiceAddress = new Address(ua3.getFirstname(),ua3.getLastname(),"Musterstrasse","541","01307","Dresden","Deutschland");
				
				order = new MealOrder(lehrer,new Debit(ua3.getFirstname()+" "+ua3.getLastname(),"1234","4321"),invoiceAddress);
				cart.addItemsTo(order);
				mealOrderManager.save(order);
		
				
				
				
	//2.Woche : 52KW / 21.12.2015 - 27.12.2015
	//Lagerbericht mit zu bestellenden Zutaten wird erstellt
				
				testDate = testDate.plusWeeks(1);
				//intern nimmt diese Methode auch alle Groceries nach FIFO aus dem Lager, welche für die aktuelle Woche benötigt werden
				StockReport report = stockManager.getStockReportForDate(testDate);
				
				//Lagerist trägt Güter ein
				stockManager.saveStockItem(stockManager.createStockItem(report.getIngredients().get(0).getGrocery(), 1.5, LocalDate.of(2016, 1, 12)));
				stockManager.saveStockItem(stockManager.createStockItem(report.getIngredients().get(1).getGrocery(), 1.5, LocalDate.of(2016, 1, 12)));
				stockManager.saveStockItem(stockManager.createStockItem(report.getIngredients().get(2).getGrocery(), 1.5, LocalDate.of(2016, 1, 12)));

				//kurzer check via console -> Lagerist hat nicht alles aufgefüllt
				report = stockManager.getStockReportForDate(testDate);
				
				//lagerist füllt ein item ausreichend auf
				stockManager.saveStockItem(stockManager.createStockItem(report.getIngredients().get(0).getGrocery(), 10.0, LocalDate.of(2016, 2, 12)));

				report = stockManager.getStockReportForDate(testDate);

				
				
				
	//3.Woche : 53KW / 28.12.2015 - 3.1.2016
	//Küche bekommt täglichen Bericht aus Rezepten und Menge der Ingredients
				
				testDate = testDate.plusWeeks(1);
				
				//via console checked
				kitchenManager.getKitchenReportForDate(testDate);
				
				
	//NOTE: Aufgrund des private Dates der Salespoint Order , ist es nicht möglich weitere Orders z.b. aus der letzten Woche 
	// zu simulieren			
				
		}
}
