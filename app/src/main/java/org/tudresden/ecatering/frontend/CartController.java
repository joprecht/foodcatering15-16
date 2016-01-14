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
		
		for(int a = number.size()-1; a >= 0; a--){
			if(number.get(a).equals(0)){
				menuitem.remove(a);
				number.remove(a);
			}
			
		}
		
		
		//Add menuItem to cart
		for(int i=0; i < menuitem.size(); i++){
		
		cart.addOrUpdateItem(menuitem.get(i), Quantity.of(number.get(i)));
		System.out.println("Added "+menuitem.get(i).getName()+" ID "+menuitem.get(i).getIdentifier());
		}
		return "redirect:/showPlan";
	}

	@RequestMapping(value = "/cart", method = RequestMethod.GET)
	public String cart(@LoggedIn Optional<UserAccount> userAccount, ModelMap modelMap) {
		
		Iterable<MealOrder> mealOrders = mealOrderManager.findBy(userAccount.get());
		Iterator<MealOrder> iter = mealOrders.iterator();
		if(iter.hasNext()){
			modelMap.addAttribute("address", iter.next().getInvoiceAddress());
		}else{
			//if no previous order is found, output an empty address
			modelMap.addAttribute("address",new Address("","","","","","",""));
		}
		
		return "cart";
	}


	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String checkout(@ModelAttribute Cart cart, 
						   @LoggedIn Optional<UserAccount> userAccount,
						   @RequestParam("streetname") String street,
						   @RequestParam("streetnumber") String number,
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
				
				Iterable<Menu> nextBig = kitchenManager.findMenusByDate(next);
				Iterator<Menu> iter2 = nextBig.iterator();
				while(iter.hasNext()){
					Menu menu2 = iter2.next();
					if(menu2.getHelping().equals(Helping.REGULAR)){
						modelMap.addAttribute("nextWeek", menu2);
					}
				}
				
				Iterable<Menu> secondNextBig = kitchenManager.findMenusByDate(secondNext);
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
				
				Iterable<Menu> nextBig = kitchenManager.findMenusByDate(next);
				Iterator<Menu> iter2 = nextBig.iterator();
				while(iter.hasNext()){
					Menu menu2 = iter2.next();
					if(menu2.getHelping().equals(Helping.SMALL)){
						modelMap.addAttribute("nextWeek", menu2);
					}
				}
				
				Iterable<Menu> secondNextBig = kitchenManager.findMenusByDate(secondNext);
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
			
			Iterable<Menu> nextBig = kitchenManager.findMenusByDate(next);
			Iterator<Menu> iter2 = nextBig.iterator();
			while(iter.hasNext()){
				Menu menu2 = iter2.next();
				if(menu2.getHelping().equals(Helping.REGULAR)){
					modelMap.addAttribute("nextWeek", menu2);
				}
			}
			
			Iterable<Menu> secondNextBig = kitchenManager.findMenusByDate(secondNext);
			Iterator<Menu> iter3 = secondNextBig.iterator();
			while(iter3.hasNext()){
				Menu menu3 = iter3.next();
				if(menu3.getHelping().equals(Helping.REGULAR)){
					modelMap.addAttribute("secondNextWeek", menu3);
				}
			}
		}
	
		return "showPlan";
	}
	
	@RequestMapping(value = "/clearCart", method = RequestMethod.POST)
	public String clearCart(@ModelAttribute Cart cart) {
			cart.clear();
		return "redirect:/cart";
	}
	
	@RequestMapping(value = "/decreaseCart", method = RequestMethod.POST)
	public String decreaseCart(@ModelAttribute Cart cart, @RequestParam("meal") String menuItem) {
		
//		if(cart.getItem(menuItem.toString()).get().getQuantity().equals(1)){
//			cart.removeItem(menuItem.toString());
//		}else{
//			cart.addOrUpdateItem(menuItem, Quantity.of(-1));
//		}
		if(cart.getItem(menuItem).get().getQuantity().equals(Quantity.of(1))){
			cart.removeItem(menuItem);
		}else{
			cart.addOrUpdateItem(cart.getItem(menuItem).get().getProduct(), Quantity.of(-1));
		}
		
		return "redirect:/cart";
	}
	
	@RequestMapping(value = "/increaseCart", method = RequestMethod.POST)
	public String increaseCart(@ModelAttribute Cart cart, @RequestParam("meal") String menuItem) {
		
		cart.addOrUpdateItem(cart.getItem(menuItem).get().getProduct(), Quantity.of(1));
		
		return "redirect:/cart";
	}
	
}
