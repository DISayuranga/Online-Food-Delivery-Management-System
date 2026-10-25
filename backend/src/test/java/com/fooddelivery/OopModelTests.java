package com.fooddelivery;

import com.fooddelivery.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CraveBite OOP Architecture & Business Logic Tests")
public class OopModelTests {

    @Test
    @DisplayName("Polymorphism: FoodItem Price Calculation for MainCourse, Dessert, Beverage")
    void testPolymorphicFoodItemPricing() {
        // MainCourse: Base + 10% prep tax
        FoodItem steak = new MainCourse();
        steak.setName("Ribeye Steak");
        steak.setBasePrice(100.0);
        assertEquals(110.0, steak.calculatePrice(), 0.001, "MainCourse should add 10% preparation tax");
        assertEquals(110.0, steak.getFinalPrice(), 0.001, "getFinalPrice() should match calculatePrice()");
        assertEquals("Main Course", steak.getItemType());

        // Dessert: Base + $2.0 sweet tax
        FoodItem cake = new Dessert();
        cake.setName("Chocolate Lava Cake");
        cake.setBasePrice(50.0);
        assertEquals(52.0, cake.calculatePrice(), 0.001, "Dessert should add flat $2.0 tax");
        assertEquals(52.0, cake.getFinalPrice(), 0.001);
        assertEquals("Dessert", cake.getItemType());

        // Beverage: Base + 5% tax + $1.0 cup fee
        FoodItem drink = new Beverage();
        drink.setName("Iced Coffee");
        drink.setBasePrice(40.0);
        // 40.0 + (40 * 0.05) + 1.0 = 40.0 + 2.0 + 1.0 = 43.0
        assertEquals(43.0, drink.calculatePrice(), 0.001, "Beverage should add 5% tax and $1.0 cup fee");
        assertEquals(43.0, drink.getFinalPrice(), 0.001);
        assertEquals("Beverage", drink.getItemType());
    }

    @Test
    @DisplayName("Polymorphism: Address Delivery Instructions for HomeAddress and OfficeAddress")
    void testPolymorphicAddressDeliveryInstructions() {
        HomeAddress home = new HomeAddress();
        home.setStreetAddress("123 Palm Grove");
        home.setCity("Colombo");
        home.setZipCode("00700");
        home.setGateCode("#4321");

        String homeInstructions = home.getDeliveryInstructions();
        assertNotNull(homeInstructions);
        assertTrue(homeInstructions.contains("front door"), "Home address should specify front door delivery");
        assertTrue(homeInstructions.contains("#4321"), "Home address should include gate code");
        assertEquals("HOME", home.getAddressType());
        assertEquals("#4321", home.getSpecificDetail());

        OfficeAddress office = new OfficeAddress();
        office.setStreetAddress("456 World Trade Center");
        office.setCity("Colombo");
        office.setZipCode("00100");
        office.setFloorAndCompany("Floor 14, TechCorp");

        String officeInstructions = office.getDeliveryInstructions();
        assertNotNull(officeInstructions);
        assertTrue(officeInstructions.contains("reception"), "Office address should specify reception delivery");
        assertTrue(officeInstructions.contains("Floor 14"), "Office address should include floor and company details");
        assertEquals("OFFICE", office.getAddressType());
        assertEquals("Floor 14, TechCorp", office.getSpecificDetail());
    }

    @Test
    @DisplayName("Polymorphism & Encapsulation: Payment Methods processing & card number masking")
    void testPaymentMethodPolymorphismAndInformationHiding() {
        CreditCard creditCard = new CreditCard();
        creditCard.setCardholderName("John Doe");
        creditCard.setCardNumber("4111222233334444");
        creditCard.setExpiryDate("12/28");
        creditCard.setCvv("123");
        creditCard.setCreditLimit(5000.0);

        // Masking test
        assertEquals("**** **** **** 4444", creditCard.getMaskedDetails(), "Card number should be masked displaying only last 4 digits");
        assertEquals("CREDIT", creditCard.getCardType());
        assertTrue(creditCard.getCardSpecificDetail().contains("5000"));

        // Polymorphic processing for Credit Card
        String creditReceipt = creditCard.processPayment(100.0);
        assertTrue(creditReceipt.contains("Visa/Mastercard"), "Credit card payment should route through Visa/Mastercard network");
        assertTrue(creditReceipt.contains("100.00"));

        DebitCard debitCard = new DebitCard();
        debitCard.setCardholderName("Jane Smith");
        debitCard.setCardNumber("5500111122228888");
        debitCard.setExpiryDate("06/27");
        debitCard.setCvv("999");
        debitCard.setBankName("Commercial Bank");

        assertEquals("**** **** **** 8888", debitCard.getMaskedDetails());
        assertEquals("DEBIT", debitCard.getCardType());
        assertEquals("Commercial Bank", debitCard.getBankName());

        // Polymorphic processing for Debit Card
        String debitReceipt = debitCard.processPayment(100.0);
        assertTrue(debitReceipt.contains("direct bank transfer"), "Debit card payment should route via bank transfer");
        assertTrue(debitReceipt.contains("Commercial Bank"));
    }

    @Test
    @DisplayName("Inheritance & Abstraction: User roles and discriminator types")
    void testUserRoleInheritance() {
        User customer = new Customer();
        customer.setUsername("cust1");
        customer.setFullName("Customer One");
        assertEquals("CUSTOMER", customer.getRoleType());

        User owner = new RestaurantOwner();
        owner.setUsername("owner1");
        owner.setFullName("Owner One");
        assertEquals("RESTAURANT_OWNER", owner.getRoleType());

        User rider = new Rider();
        rider.setUsername("rider1");
        rider.setFullName("Rider One");
        assertEquals("RIDER", rider.getRoleType());

        User admin = new Admin();
        admin.setUsername("admin1");
        admin.setFullName("Admin One");
        assertEquals("ADMIN", admin.getRoleType());
    }

    @Test
    @DisplayName("Inheritance: Review types for FoodReview and DeliveryReview")
    void testReviewTypes() {
        Review foodReview = new FoodReview();
        assertEquals("FOOD", foodReview.getReviewType());

        Review deliveryReview = new DeliveryReview();
        assertEquals("DELIVERY", deliveryReview.getReviewType());
    }

    @Test
    @DisplayName("Cart Calculations: Quantity updates and polymorphic total price computation")
    void testCartItemCalculations() {
        FoodItem item = new MainCourse();
        item.setName("Fried Rice");
        item.setBasePrice(120.0); // calculatePrice = 120 + 10% = 132.0

        CartItem cartItem = new CartItem();
        cartItem.setFoodItem(item);
        cartItem.setQuantity(3);

        // 132.0 * 3 = 396.0
        assertEquals(396.0, cartItem.getTotalPrice(), 0.001, "Cart item total should use polymorphic calculatePrice() * quantity");
    }
}
