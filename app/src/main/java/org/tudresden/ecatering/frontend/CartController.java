package org.tudresden.ecatering.frontend;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import org.javamoney.moneta.Money;
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
import org.tudresden.ecatering.model.accountancy.Discount;
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
import static org.salespointframework.core.Currencies.EURO;

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

	/**
	 * Controller to add MenuItems to the customers cart
	 * 
	 * @param menuitem Array of MenuItems (their identifier) the customer wants to buy
	 * @param number Array of Integers according to the menuitem Array, so that we know how many of each menuitem the customer wants
	 * @param cart The cart itself
	 * @return cart.html
	 */
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
		return "redirect:/cart";
	}

	/**
	 * Controller to display the users current cart
	 * Also displays an address if the user previously ordered
	 * If the user is registered to childcare and one of the caretakers he also gets an discount
	 * 
	 * @param userAccount needed to find out if User is registered to childcare or company
	 * @param modelMap Needed for Thymeleaf
	 * @param cart The cart itself
	 * @return cart.html
	 */
	@RequestMapping(value = "/cart", method = RequestMethod.GET)
	public String cart(@LoggedIn Optional<UserAccount> userAccount, ModelMap modelMap, @ModelAttribute Cart cart) {
		
		Money discountPrice = null;
		
		Iterable<MealOrder> mealOrders = mealOrderManager.findBy(userAccount.get());
		Iterator<MealOrder> iter = mealOrders.iterator();
		if(iter.hasNext()){
			modelMap.addAttribute("address", iter.next().getInvoiceAddress());
		}else{
			//if no previous order is found, output an empty address
			modelMap.addAttribute("address",new Address("","","","","","",""));
		}
		Discount discount = customerManager.findCustomerByUserAccount(userAccount.get()).get().getDiscount();
		if(discount.equals("Discount.CHILDCARE")){
			double tempValue = cart.getPrice().getNumberStripped().multiply(BigDecimal.valueOf(discount.getDiscountFactor())).doubleValue();
			discountPrice = Money.of(BigDecimal.valueOf(tempValue).setScale(2, BigDecimal.ROUND_HALF_DOWN), EURO);
		}
		
		modelMap.addAttribute("discountPrice", discountPrice);
		modelMap.addAttribute("discount", 100-(Discount.CHILDCARE.getDiscountFactor()*100));
		
		
		return "cart";
	}


	/**
	 * Controller to actually Buy the stuff in the cart
	 * 
	 * @param cart Cart itself
	 * @param userAccount userAccount of the current User
	 * @param street Streetname of current User
	 * @param number Streetnumber of current User
	 * @param zip Zip code of current User
	 * @param city City of current User
	 * @param country Country of current User
	 * @param payment Prefered payment method of current User
	 * @param iban If payment is transfer this is required
	 * @param bic If payment is transfer this is required
	 * @return cart.html
	 */
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
	
	/**
	 * Controller to show three menus from today to two weeks from now
	 * Depending on who is logged in the wil see either Regular or Small size Menus
	 * 
	 * @param modelMap Required for Thymeleaf
	 * @param userAccount The current user
	 * @return showPlan.html
	 */
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
	
	/**
	 * Controller to delete the current cart if the user chooses to do so
	 * 
	 * @param cart The users cart
	 * @return cart.html
	 */
	@RequestMapping(value = "/clearCart", method = RequestMethod.POST)
	public String clearCart(@ModelAttribute Cart cart) {
			cart.clear();
		return "redirect:/cart";
	}
	
	/**
	 * Controller to decrease the quantity of one MenuItem in the cart by one
	 * or delte it if it goes from one to zero
	 * 
	 * @param cart The users current cart
	 * @param menuItem The menuItem that should be reduced/deleted
	 * @see increaseCart
	 * @return
	 */
	@RequestMapping(value = "/decreaseCart", method = RequestMethod.POST)
	public String decreaseCart(@ModelAttribute Cart cart, @RequestParam("meal") String menuItem) {
		
		if(cart.getItem(menuItem).get().getQuantity().equals(Quantity.of(1))){
			cart.removeItem(menuItem);
		}else{
			cart.addOrUpdateItem(cart.getItem(menuItem).get().getProduct(), Quantity.of(-1));
		}
		
		return "redirect:/cart";
	}
	
	/**
	 * Controller to increase the quantity of one MneuItem in teh cart by one
	 * 
	 * @param cart Current users cart
	 * @param menuItem The menuItem that should be increased by one
	 * @see decreaseCart
	 * @return cart.html
	 */
	@RequestMapping(value = "/increaseCart", method = RequestMethod.POST)
	public String increaseCart(@ModelAttribute Cart cart, @RequestParam("meal") String menuItem) {
		
		cart.addOrUpdateItem(cart.getItem(menuItem).get().getProduct(), Quantity.of(1));
		
		return "redirect:/cart";
	}
	
}
