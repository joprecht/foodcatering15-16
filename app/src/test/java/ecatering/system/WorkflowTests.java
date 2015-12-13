package ecatering.system;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.business.BusinessManager;
import org.tudresden.ecatering.model.customer.CustomerManager;
import org.tudresden.ecatering.model.kitchen.DailyMenu;
import org.tudresden.ecatering.model.kitchen.Day;
import org.tudresden.ecatering.model.kitchen.Helping;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MealType;
import org.tudresden.ecatering.model.kitchen.MenuItem;
import org.tudresden.ecatering.model.stock.StockManager;

import ecatering.AbstractIntegrationTests;

public class WorkflowTests extends AbstractIntegrationTests {

	
		@Autowired BusinessManager businessManager;
		@Autowired UserAccountManager userAccountManager;
		@Autowired CustomerManager customerManager;
		@Autowired StockManager stockManager;
		@Autowired KitchenManager kitchenManager;

		
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
				customerManager.saveCustomer(customerManager.createCustomer(ua1, "kundenCode-1234"));

				
		//Kunde Kinderbetreuung(Kind)
				UserAccount ua2 = userAccountManager.create("kind", "123", Role.of("ROLE_CUSTOMER"));
				ua1.setFirstname("Fabian");
				ua1.setLastname("Schaller");
				ua1.setEmail("fabian@schaller.de");

			assertTrue(businessManager.findBusinessByCode("elternCode-1234").isPresent());

				userAccountManager.save(ua2);
				customerManager.saveCustomer(customerManager.createCustomer(ua2, "elternCode-1234"));
				
			
	
		//Kunde Kinderbetreuung(Lehrer)
				UserAccount ua3 = userAccountManager.create("lehrer", "123", Role.of("ROLE_CUSTOMER"));
				ua1.setFirstname("Maria");
				ua1.setLastname("Musterfrau");
				ua1.setEmail("maria@musterfrau.de");

			assertTrue(businessManager.findBusinessByCode("lehrerCode-1234").isPresent());

				userAccountManager.save(ua3);
				customerManager.saveCustomer(customerManager.createCustomer(ua3, "elternCode-1234"));
		
				
	//im Lager trägt der Lagerist über Controller(role_stock) alle Lebensmittel ein, welche über einen Zulieferer
	//beschafft werden können	
				
				//kurzer check - im DataInitializer wurden schon 5 Lebensmittel hinzugefügt
				//Lager ist noch leer
				assertThat(stockManager.findAllGroceries(), is(iterableWithSize(5)));
				assertThat(stockManager.findAllStockItems(), is(iterableWithSize(0)));

				
	//in der Küche(Controller(role_kitchen)) kann nun der Koch Rezepte erstellen, basierend auf den verfügbaren Lebensmittel
		
				//kurzer check - im DataInitializer wurden schon 3 Rezepte bestehend aus den 5 Lebensmitteln erstellt
				assertThat(kitchenManager.findAllRecipes(), is(iterableWithSize(3)));
				
				
				
	//der Buchhalter (Controller(role_accounting) schaut sich die Rezepte an, und entscheidet welches eine feste
	//Mahlzeit werden soll. Dabei gibt er auch einen Faktor für Gewinn an. Es wird zudem auch ein Speiseplan erstellt.
				
				//kurzer check - im DataInitializer wurden aus allen Rezepten schon Mahlzeiten gebaut
				assertThat(kitchenManager.findAllMeals(), is(iterableWithSize(3)));			
				assertThat(kitchenManager.findAllMenus(), is(iterableWithSize(0)));
				
				List<MenuItem> mondayMeals = new ArrayList<MenuItem>();
				mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR));
				mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR));
				mondayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR));

				List<MenuItem> tuesdayMeals = new ArrayList<MenuItem>();
				tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR));
				tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR));
				tuesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR));
				
				List<MenuItem> wednesdayMeals = new ArrayList<MenuItem>();
				wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR));
				wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR));
				wednesdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR));

				List<MenuItem> thursdayMeals = new ArrayList<MenuItem>();
				thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR));
				thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR));
				thursdayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR));
				
				List<MenuItem> fridayMeals = new ArrayList<MenuItem>();
				fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.REGULAR).iterator().next(),Helping.REGULAR));
				fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.SPECIAL).iterator().next(),Helping.REGULAR));
				fridayMeals.add(kitchenManager.createMenuItem(kitchenManager.findMealsByMealType(MealType.DIET).iterator().next(),Helping.REGULAR));
					
				List<DailyMenu> dailyMenus = new ArrayList<DailyMenu>();
				dailyMenus.add(kitchenManager.createDailyMenu(Day.MONDAY, mondayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(Day.TUESDAY, tuesdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(Day.WEDNESDAY, wednesdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(Day.THURSDAY, thursdayMeals));
				dailyMenus.add(kitchenManager.createDailyMenu(Day.FRIDAY, fridayMeals));

				kitchenManager.saveMenu(kitchenManager.createMenu(45, dailyMenus));
				
				
//##########   Ablaufplan für 4 Wochen ########//				
				
	//1.Woche : 43KW / 19.10.2015 - 25.10.2015
	//Ein Speiseplan wird dem Kunden gezeigt, woraufhin dieser für die übernächste Woche bestellen kann
		
				LocalDate testDate = LocalDate.of(2015, 10, 19);
				
				//Speiseplan für übernächste Woche
				assertThat(kitchenManager.findMenusByDate(testDate.plusWeeks(2)),is(iterableWithSize(1)));

				
				

		}
}
