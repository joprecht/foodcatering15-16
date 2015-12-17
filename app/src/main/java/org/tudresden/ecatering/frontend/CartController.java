package org.tudresden.ecatering.frontend;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;


import org.salespointframework.order.Cart;
import org.salespointframework.order.OrderManager;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.tudresden.ecatering.model.accountancy.Address;
import org.tudresden.ecatering.model.accountancy.Debit;
import org.tudresden.ecatering.model.accountancy.MealOrder;
import org.tudresden.ecatering.model.accountancy.Transfer;
import org.tudresden.ecatering.model.business.Business;
import org.tudresden.ecatering.model.business.BusinessType;
import org.tudresden.ecatering.model.customer.Customer;
import org.tudresden.ecatering.model.customer.CustomerManager;
import org.tudresden.ecatering.model.kitchen.Helping;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.Menu;
import org.tudresden.ecatering.model.kitchen.MenuItem;

@Controller
@PreAuthorize("hasRole('ROLE_ACCOUNTING')||hasRole('ROLE_CUSTOMER')")
@SessionAttributes("cart")
class CartController {


	private final KitchenManager kitchenManager;
	private final CustomerManager customerManager;
	private final OrderManager<MealOrder> mealOrderManager;

	@Autowired
	public CartController(KitchenManager kitchenManager, CustomerManager customerManager, OrderManager<MealOrder> mealOrderManager) {

		this.kitchenManager = kitchenManager;
		this.customerManager = customerManager;
		this.mealOrderManager = mealOrderManager;
	}

	@ModelAttribute("cart")
	public Cart initializeCart() {
		return new Cart();
	}

	
	@RequestMapping(value = "/cart", method = RequestMethod.POST)
	public String addMeals(@RequestParam("meal") ArrayList<MenuItem> menuitem, @RequestParam("number") ArrayList<Integer> number, @ModelAttribute Cart cart) {
		
		//Add menuItem to cart
		for(int i=0; i < menuitem.size(); i++){
		
		cart.addOrUpdateItem(menuitem.get(i), Quantity.of(number.get(i)));
		}
		return "";
	}

	@RequestMapping(value = "/cart", method = RequestMethod.GET)
	public String cart() {
		return "cart";
	}


	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String checkout(@ModelAttribute Cart cart, 
						   @LoggedIn Optional<UserAccount> userAccount,
						   @RequestParam("streetname") String street,
						   @RequestParam("streenumber") String number,
						   @RequestParam("zip") String zip,
						   @RequestParam("city") String city,
						   @RequestParam("country") String country,
						   @RequestParam("payment") String payment,
						   @RequestParam(value = "iban", required = false) String iban,
						   @RequestParam(value = "ibic", required = false) String bic) {

		UserAccount user = userAccount.get();
		Customer cust = customerManager.findCustomerByUserAccount(user).get();
		Address invoiceAddress = new Address(user.getFirstname(),user.getLastname(),street,number,zip,city,country);
		
		return userAccount.map(account -> {
			MealOrder order = null;
			
			if(payment.equals("TRANSFER")){
				order = new MealOrder(cust, Transfer.TRANSFER,invoiceAddress);	
			}else{
				order = new MealOrder(cust,new Debit(user.getFirstname()+" "+user.getLastname(),iban,bic),invoiceAddress);	
			}
			

			cart.addItemsTo(order);
			mealOrderManager.save(order);

//			orderManager.payOrder(order);
//			orderManager.completeOrder(order);

			cart.clear();

			return "redirect:/";
		}).orElse("redirect:/cart");
	}
	
	//TODO HTML for showing the Menus of the following 3 weeks
	@RequestMapping("/showPlan")
	public String showPlan(ModelMap modelMap, @LoggedIn Optional<UserAccount> userAccount){
		
		//get the next 3 weeks
		LocalDate now = LocalDate.now();
		LocalDate next = now.plusWeeks(1);
		LocalDate secondNext = next.plusWeeks(1);
		
		
		Optional<Customer> cust = customerManager.findCustomerByUserAccount(userAccount.get());
		if(cust.isPresent()){
			Customer cust2 = cust.get();
			Business business = cust2.getBusiness();
			
			if(business.getBusinessType().equals(BusinessType.COMPANY)){
				System.out.println("Company");
				Iterable<Menu> currentBig = kitchenManager.findMenusByDate(now);
				Iterator<Menu> iter = currentBig.iterator();
				while(iter.hasNext()){
					Menu menu = iter.next();
					if(menu.getHelping().equals(Helping.REGULAR)){
						modelMap.addAttribute("currentWeek", menu);
					}
				}
				
				Iterable<Menu> nextBig = kitchenManager.findMenusByDate(secondNext);
				Iterator<Menu> iter2 = nextBig.iterator();
				while(iter.hasNext()){
					Menu menu2 = iter2.next();
					if(menu2.getHelping().equals(Helping.REGULAR)){
						modelMap.addAttribute("nextWeek", menu2);
					}
				}
				
				Iterable<Menu> secondNextBig = kitchenManager.findMenusByDate(now);
				Iterator<Menu> iter3 = secondNextBig.iterator();
				while(iter3.hasNext()){
					Menu menu3 = iter3.next();
					if(menu3.getHelping().equals(Helping.REGULAR)){
						modelMap.addAttribute("secondNextWeek", menu3);
					}
				}
			}else{
				System.out.println("Social");
				
				Iterable<Menu> currentBig = kitchenManager.findMenusByDate(now);
				Iterator<Menu> iter = currentBig.iterator();
				while(iter.hasNext()){
					Menu menu = iter.next();
					if(menu.getHelping().equals(Helping.SMALL)){
						modelMap.addAttribute("currentWeek", menu);
					}
				}
				
				Iterable<Menu> nextBig = kitchenManager.findMenusByDate(secondNext);
				Iterator<Menu> iter2 = nextBig.iterator();
				while(iter.hasNext()){
					Menu menu2 = iter2.next();
					if(menu2.getHelping().equals(Helping.SMALL)){
						modelMap.addAttribute("nextWeek", menu2);
					}
				}
				
				Iterable<Menu> secondNextBig = kitchenManager.findMenusByDate(now);
				Iterator<Menu> iter3 = secondNextBig.iterator();
				while(iter3.hasNext()){
					Menu menu3 = iter3.next();
					if(menu3.getHelping().equals(Helping.SMALL)){
						modelMap.addAttribute("secondNextWeek", menu3);
					}
				}
			}
		}else{
			Iterable<Menu> currentBig = kitchenManager.findMenusByDate(now);
			Iterator<Menu> iter = currentBig.iterator();
			while(iter.hasNext()){
				Menu menu = iter.next();
				if(menu.getHelping().equals(Helping.REGULAR)){
					modelMap.addAttribute("currentWeek", menu);
				}
			}
			
			Iterable<Menu> nextBig = kitchenManager.findMenusByDate(secondNext);
			Iterator<Menu> iter2 = nextBig.iterator();
			while(iter.hasNext()){
				Menu menu2 = iter2.next();
				if(menu2.getHelping().equals(Helping.REGULAR)){
					modelMap.addAttribute("nextWeek", menu2);
				}
			}
			
			Iterable<Menu> secondNextBig = kitchenManager.findMenusByDate(now);
			Iterator<Menu> iter3 = secondNextBig.iterator();
			while(iter3.hasNext()){
				Menu menu3 = iter3.next();
				if(menu3.getHelping().equals(Helping.REGULAR)){
					modelMap.addAttribute("secondNextWeek", menu3);
				}
			}
		}
		
	
		//Map the Menus according to the weeks
		//modelMap.addAttribute("currentWeek", kitchenManager.findMenusByDate(now));
		//modelMap.addAttribute("nextWeek", kitchenManager.findMenusByDate(next));
		//modelMap.addAttribute("afterNextWeek", kitchenManager.findMenusByDate(nextnext));
		return "showPlan";
	}
}
