/**
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package videoshop.controller;

import videoshop.model.Disc;
import java.util.Optional;

import org.springframework.ui.Model;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.core.AbstractEntity;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * A Spring MVC controller to manage the {@link Cart}. {@link Cart} instances are held in the session as they're
 * specific to a certain user. That's also why the entire controller is secured by a {@code hasRole(…)} clause.
 *
 * @author Paul Henke
 * @author Oliver Gierke
 */
@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
class CartController {

	private final OrderManager<Order> orderManager;
	private final Inventory<InventoryItem> inventory;
	private static final Quantity NONE = Quantity.of(0);
	
	/**
	 * Creates a new {@link CartController} with the given {@link OrderManager}.
	 * 
	 * @param orderManager must not be {@literal null}.
	 */
	@Autowired
	public CartController(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory) {

		Assert.notNull(orderManager, "OrderManager must not be null!");
		this.orderManager = orderManager;
		this.inventory = inventory;
	}

	/**
	 * Creates a new {@link Cart} instance to be stored in the session (see the class-level {@link SessionAttributes}
	 * annotation).
	 * 
	 * @return a new {@link Cart} instance.
	 */
	@ModelAttribute("cart")
	public Cart initializeCart() {
		return new Cart();
	}

	/**
	 * Adds a {@link Disc} to the {@link Cart}. Note how the type of the parameter taking the request parameter
	 * {@code pid} is {@link Disc}. For all domain types extening {@link AbstractEntity} (directly or indirectly) a tiny
	 * Salespoint extension will directly load the object instance from the database. If the identifier provided is
	 * invalid (invalid format or no {@link Product} with the id found), {@literal null} will be handed into the method.
	 * 
	 * @param disc
	 * @param number
	 * @param session
	 * @param modelMap
	 * @return
	 */
	//@RequestMapping(value = "/reorderItem", method = RequestMethod.GET)
	@RequestMapping(value = "/cart", method = RequestMethod.POST)
	public String addDisc(@RequestParam("pid") Disc disc, @RequestParam("number") int number, @ModelAttribute Cart cart, Model model) {

		// (｡◕‿◕｡)
		// Das Inputfeld im View ist eigentlich begrenzt, allerdings sollte man immer Clientseitig validieren
		//int amount = number <= 0 || number > 100 ? 1 : number;
		
		//InventoryItem item = inventory.findByProductIdentifier(disc.getId()).get();
		Optional<InventoryItem> item = inventory.findByProductIdentifier(disc.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);
		Quantity order = item.map(InventoryItem::getQuantity).orElse(NONE);
		int inStock = quantity.getAmount().intValue();

		if(inStock <= 0){
			model.addAttribute("disc", disc);
			model.addAttribute("quantity", quantity);
			model.addAttribute("orderable", quantity.isGreaterThan(Quantity.of(0)));
			model.addAttribute("errorKey", "error.notEnough");
			return "detail";
			}
		int amount = number <= 0 || number > inStock ? inStock : number;

		
//		if(!a.hasSufficientQuantity(order)){
//			model.addAttribute("disc", disc);
//			model.addAttribute("quantity", order);
//			model.addAttribute("orderable", order.isGreaterThan(Quantity.of(0)));
//			model.addAttribute("errorKey", "error.not enough");
//			return "detail";
//		}
		
		
	
		
		// (｡◕‿◕｡)
		// Eine OrderLine besteht aus einem Produkt und einer Quantity, diese kann auch direkt in eine Order eingefügt
		// werden
		cart.addOrUpdateItem(disc, Quantity.of(amount));
		item.get().decreaseQuantity(Quantity.of(amount));
		inventory.save(item.get());

		

		// (｡◕‿◕｡)
		// Je nachdem ob disc eine Dvd oder eine Bluray ist, leiten wir auf die richtige Seite weiter
		//hier füge ich den Fehlerfall ein

		switch (disc.getType()) {
			case DVD:
				return "redirect:dvdCatalog";
			case BLURAY:
			default:
				return "redirect:blurayCatalog";
		}
	}
	
	@RequestMapping(value = "reorderItem", method = RequestMethod.GET)
	public String reorderItem(@ModelAttribute Cart cart,@RequestParam("pid") Disc disc, @RequestParam("number") int number, Model model){
		int amount = number <= 0 || number > 100 ? 1 : number;
		InventoryItem a = inventory.findByProductIdentifier(disc.getId()).get();
		//Quantity.of(a) = Quantity.of(10);	
		model.addAttribute("orderable", Quantity.of(amount).isGreaterThanOrEqualTo(a.getQuantity()));
		model.addAttribute("disc", disc);
		model.addAttribute("quantity", a.getQuantity());
		//inventory.save(a.get());
		//Quantity.of(0).isGreaterThanOrEqualTo(a.getQuantity());
		return "redirect:detail/" + disc.getIdentifier();
	}

	@RequestMapping(value = "/cart", method = RequestMethod.GET)
	public String basket() {
		return "cart";
	}

	/**
	 * Checks out the current state of the {@link Cart}. Using a method parameter of type {@code Optional<UserAccount>}
	 * annotated with {@link LoggedIn} you can access the {@link UserAccount} of the currently logged in user.
	 * 
	 * @param session must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @return
	 */
	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount) {

		return userAccount.map(account -> {

			// (｡◕‿◕｡)
			// Mit commit wird der Warenkorb in die Order überführt, diese wird dann bezahlt und abgeschlossen.
			// Orders können nur abgeschlossen werden, wenn diese vorher bezahlt wurden.
			Order order = new Order(account, Cash.CASH);

			cart.addItemsTo(order);

			orderManager.payOrder(order);
			orderManager.completeOrder(order);

			cart.clear();

			return "redirect:/";
		}).orElse("redirect:/cart");
	}
}
