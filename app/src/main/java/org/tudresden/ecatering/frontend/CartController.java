package org.tudresden.ecatering.frontend;


import java.time.LocalDate;
import java.util.Optional;

import org.salespointframework.catalog.Product;
import org.salespointframework.core.AbstractEntity;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.tudresden.ecatering.model.kitchen.KitchenManager;
import org.tudresden.ecatering.model.kitchen.MenuItem;

@Controller
@PreAuthorize("hasRole('ROLE_ACCOUNTING')||hasRole('ROLE_CUSTOMER')")
@SessionAttributes("cart")
class CartController {

	private final OrderManager<Order> orderManager;
	private final KitchenManager kitchenManager;

	@Autowired
	public CartController(OrderManager<Order> orderManager, KitchenManager kitchenManager) {

		Assert.notNull(orderManager, "OrderManager must not be null!");
		this.orderManager = orderManager;
		this.kitchenManager = kitchenManager;
	}

	@ModelAttribute("cart")
	public Cart initializeCart() {
		return new Cart();
	}

	
	@RequestMapping(value = "/cart", method = RequestMethod.POST)
	public String addDisc(@RequestParam("meal") MenuItem menuitem, @RequestParam("number") int number, @ModelAttribute Cart cart) {
		
		//Add menuItem to cart
		cart.addOrUpdateItem(menuitem, Quantity.of(number));

		return "";
	}

	@RequestMapping(value = "/cart", method = RequestMethod.GET)
	public String basket() {
		return "cart";
	}


	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount) {

		return userAccount.map(account -> {


			Order order = new Order(account, Cash.CASH);

			cart.addItemsTo(order);

			orderManager.payOrder(order);
			orderManager.completeOrder(order);

			cart.clear();

			return "redirect:/";
		}).orElse("redirect:/cart");
	}
	
	//TODO HTML for showing the Menus of the foolowing 3 weeks
	@RequestMapping("/showPlan")
	public String showPlan(ModelMap modelMap){
		
		//get the next 3 weeks
		LocalDate now = LocalDate.now();
		LocalDate next = now.plusWeeks(1);
		LocalDate nextnext = next.plusWeeks(1);
	
		//Map the Menus according to the weeks
		modelMap.addAttribute("currentWeek", kitchenManager.findMenusByDate(now));
		modelMap.addAttribute("nextWeek", kitchenManager.findMenusByDate(next));
		modelMap.addAttribute("afternextWeek", kitchenManager.findMenusByDate(nextnext));
		return "showPlan";
	}
}
