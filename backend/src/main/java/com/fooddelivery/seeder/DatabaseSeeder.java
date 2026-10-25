package com.fooddelivery.seeder;

import com.fooddelivery.model.*;
import com.fooddelivery.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            com.fooddelivery.model.Admin admin = new com.fooddelivery.model.Admin();
            admin.setUsername("admin");
            admin.setEmail("admin@test.com");
            admin.setPassword("password");
            admin.setFullName("Super Admin");
            userRepository.save(admin);
            System.out.println("Admin user seeded.");
        }

        Customer customer = (Customer) userRepository.findByUsername("customer").orElse(null);
        RestaurantOwner owner = (RestaurantOwner) userRepository.findByUsername("owner").orElse(null);

        if (customer == null) {
            customer = new Customer();
            customer.setUsername("customer");
            customer.setEmail("customer@test.com");
            customer.setPassword("password");
            customer.setFullName("Alice Customer");
            customer.setDefaultDeliveryAddress("123 Test St, Colombo");
            customer.setPhoneNumber("0771234567");
            customer = userRepository.save(customer);
        }

        if (owner == null) {
            owner = new RestaurantOwner();
            owner.setUsername("owner");
            owner.setEmail("owner@test.com");
            owner.setPassword("password");
            owner.setFullName("Bob Owner");
            owner.setRestaurantName("Bob's Burgers & Bistro");
            owner = userRepository.save(owner);
        }

        if (userRepository.findByUsername("rider").isEmpty()) {
            Rider rider = new Rider();
            rider.setUsername("rider");
            rider.setEmail("rider@test.com");
            rider.setPassword("password");
            rider.setFullName("Charlie Rider");
            rider.setPhoneNumber("0779988776");
            rider.setVehiclePlateNumber("WP-CA-9876");
            rider.setVehicleType("BIKE");
            rider.setVehicleModel("Yamaha FZ 150");
            rider.setVehicleColor("Midnight Blue");
            rider.setVehicleImageUrl("https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=500&q=80");
            rider.setVehicleApprovalStatus("APPROVED");
            rider.setVehicleAdminNotes("Verified by Super Admin");
            userRepository.save(rider);
            System.out.println("Default users seeded.");
        }

        // Seed Sample Food Items demonstrating OOP Polymorphism
        if (foodItemRepository.count() == 0 && owner != null) {
            MainCourse burger = new MainCourse();
            burger.setName("Signature Wagyu Burger");
            burger.setDescription("Juicy Wagyu patty, aged cheddar, crisp lettuce, and artisanal brioche bun.");
            burger.setBasePrice(1200.00);
            burger.setImageUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=500&q=80");
            burger.setAvailable(true);
            burger.setOwner(owner);
            foodItemRepository.save(burger);

            MainCourse pizza = new MainCourse();
            pizza.setName("Artisan Woodfired Pizza");
            pizza.setDescription("Crispy sourdough crust, San Marzano tomatoes, buffalo mozzarella, and fresh basil.");
            pizza.setBasePrice(1800.00);
            pizza.setImageUrl("https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=500&q=80");
            pizza.setAvailable(true);
            pizza.setOwner(owner);
            foodItemRepository.save(pizza);

            Dessert lavaCake = new Dessert();
            lavaCake.setName("Belgian Chocolate Lava Cake");
            lavaCake.setDescription("Decadent dark chocolate molten cake with a warm flowing core.");
            lavaCake.setBasePrice(650.00);
            lavaCake.setImageUrl("https://images.unsplash.com/photo-1563729784474-d77dbb938281?auto=format&fit=crop&w=500&q=80");
            lavaCake.setAvailable(true);
            lavaCake.setOwner(owner);
            foodItemRepository.save(lavaCake);

            Beverage icedTea = new Beverage();
            icedTea.setName("Ceylon Passion Iced Tea");
            icedTea.setDescription("Refreshing chilled black tea infused with natural passion fruit and mint.");
            icedTea.setBasePrice(350.00);
            icedTea.setImageUrl("https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=500&q=80");
            icedTea.setAvailable(true);
            icedTea.setOwner(owner);
            foodItemRepository.save(icedTea);

            System.out.println("Polymorphic Food Items seeded.");
        }

        // Seed Sample Address for Customer if none exists
        if (customer != null && addressRepository.findByCustomer(customer).isEmpty()) {
            HomeAddress home = new HomeAddress();
            home.setStreetAddress("123 Lotus Road");
            home.setCity("Colombo");
            home.setZipCode("00100");
            home.setGateCode("#4022");
            home.setLatitude(6.9271);
            home.setLongitude(79.8612);
            home.setCustomer(customer);
            addressRepository.save(home);

            OfficeAddress office = new OfficeAddress();
            office.setStreetAddress("50 Galle Road");
            office.setCity("Colombo");
            office.setZipCode("00300");
            office.setFloorAndCompany("Level 8, TechHub HQ");
            office.setLatitude(6.9147);
            office.setLongitude(79.8519);
            office.setCustomer(customer);
            addressRepository.save(office);

            System.out.println("Polymorphic Addresses seeded.");
        }

        // Seed Sample Payment Methods for Customer if none exists
        if (customer != null && paymentMethodRepository.findByCustomer(customer).isEmpty()) {
            CreditCard card = new CreditCard();
            card.setCardholderName("Alice Customer");
            card.setCardNumber("4111222233334444");
            card.setExpiryDate("12/28");
            card.setCvv("123");
            card.setCreditLimit(50000.00);
            card.setCustomer(customer);
            paymentMethodRepository.save(card);

            DebitCard debit = new DebitCard();
            debit.setCardholderName("Alice Customer");
            debit.setCardNumber("5200111122223333");
            debit.setExpiryDate("06/27");
            debit.setCvv("456");
            debit.setBankName("Commercial Bank");
            debit.setCustomer(customer);
            paymentMethodRepository.save(debit);

            System.out.println("Polymorphic Payment Methods seeded.");
        }
    }
}
