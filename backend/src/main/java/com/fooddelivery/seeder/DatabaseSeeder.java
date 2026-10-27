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

        // Seed Sample Food Items demonstrating OOP Polymorphism & Categorization
        if (owner != null) {
            // Update any existing items with appropriate categories
            for (FoodItem item : foodItemRepository.findAll()) {
                String n = " " + item.getName().toLowerCase().replaceAll("[^a-z0-9 ]", " ") + " ";
                if (n.contains(" burger ")) {
                    item.setCategory("Burgers");
                    foodItemRepository.save(item);
                } else if (n.contains(" pizza ")) {
                    item.setCategory("Pizza");
                    foodItemRepository.save(item);
                } else if (n.contains(" sushi ")) {
                    item.setCategory("Sushi");
                    foodItemRepository.save(item);
                } else if (n.contains(" steak ") || n.contains(" dinner ") || n.contains(" ribeye ")) {
                    item.setCategory("Dinner");
                    foodItemRepository.save(item);
                } else if (n.contains(" lunch ") || n.contains(" bento ")) {
                    item.setCategory("Lunch");
                    foodItemRepository.save(item);
                } else if (n.contains(" juice ")) {
                    item.setCategory("Juice");
                    foodItemRepository.save(item);
                } else if (n.contains(" pancake") || n.contains(" breakfast ") || n.contains(" eag ") || n.contains(" egg ")) {
                    item.setCategory("Breakfast");
                    foodItemRepository.save(item);
                } else if (n.contains(" cake ") || n.contains(" lava ") || item instanceof Dessert) {
                    item.setCategory("Desserts");
                    foodItemRepository.save(item);
                } else if (n.contains(" tea ") || n.contains(" smoothie ") || item instanceof Beverage) {
                    item.setCategory("Beverages");
                    foodItemRepository.save(item);
                } else if (n.contains(" salad ") || n.contains(" bowl ") || n.contains(" quinoa ")) {
                    item.setCategory("Healthy");
                    foodItemRepository.save(item);
                }
            }

            // Ensure we have items for every category in the explore section
            seedDishIfMissing("Burgers", "Signature Wagyu Burger", "Juicy Wagyu patty, aged cheddar, crisp lettuce, and artisanal brioche bun.", 1200.00, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=500&q=80", "MAIN_COURSE", owner);
            seedDishIfMissing("Pizza", "Artisan Woodfired Pizza", "Crispy sourdough crust, San Marzano tomatoes, buffalo mozzarella, and fresh basil.", 1800.00, "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=500&q=80", "MAIN_COURSE", owner);
            seedDishIfMissing("Lunch", "Executive Teriyaki Bento Lunch", "Grilled teriyaki chicken, fragrant jasmine rice, steamed broccoli, and tamagoyaki.", 1350.00, "https://images.unsplash.com/photo-1543339308-43e59d6b73a6?auto=format&fit=crop&w=500&q=80", "MAIN_COURSE", owner);
            seedDishIfMissing("Dinner", "Herb-Crusted Ribeye Steak Dinner", "Prime ribeye cut served with roasted potatoes, rosemary garlic butter, and red wine demi-glace.", 2750.00, "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=500&q=80", "MAIN_COURSE", owner);
            seedDishIfMissing("Juice", "Fresh Cold-Pressed Orange Juice", "100% natural freshly squeezed Valencia oranges with no added sugars or preservatives.", 480.00, "https://images.unsplash.com/photo-1613478223719-2ab802602423?auto=format&fit=crop&w=500&q=80", "BEVERAGE", owner);
            seedDishIfMissing("Sushi", "Dragon Roll & Salmon Nigiri Combo", "Fresh Atlantic salmon, tempura shrimp, avocado, tobiko, and unagi glaze.", 2400.00, "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?auto=format&fit=crop&w=500&q=80", "MAIN_COURSE", owner);
            seedDishIfMissing("Healthy", "Avocado & Quinoa Crunch Bowl", "Organic baby greens, toasted quinoa, Hass avocado, edamame, and sesame ginger dressing.", 1450.00, "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=500&q=80", "MAIN_COURSE", owner);
            seedDishIfMissing("Desserts", "Belgian Chocolate Lava Cake", "Decadent dark chocolate molten cake with a warm flowing core and vanilla glaze.", 650.00, "https://images.unsplash.com/photo-1551024709-8f23befc6f87?auto=format&fit=crop&w=500&q=80", "DESSERT", owner);
            seedDishIfMissing("Breakfast", "Fluffy Buttermilk Pancakes & Maple Bacon", "Golden stacked pancakes topped with whipped honey butter and crispy applewood smoked bacon.", 950.00, "https://images.unsplash.com/photo-1559847844-5315695dadae?auto=format&fit=crop&w=500&q=80", "MAIN_COURSE", owner);
            seedDishIfMissing("Beverages", "Ceylon Passion Iced Tea", "Refreshing chilled black tea infused with natural passion fruit and garden mint.", 350.00, "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=500&q=80", "BEVERAGE", owner);
            seedDishIfMissing("Main Course", "Sri Lankan Chicken Kottu Roti", "Chopped flatbread wok-tossed with spicy marinated chicken, scrambled eggs, and fragrant curry sauce.", 1100.00, "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=500&q=80", "MAIN_COURSE", owner);

            System.out.println("Polymorphic & Categorized Food Items seeded.");
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

    private void seedDishIfMissing(String category, String name, String desc, double price, String imgUrl, String type, RestaurantOwner owner) {
        boolean exists = foodItemRepository.findAll().stream()
                .anyMatch(f -> f.getName().equalsIgnoreCase(name));
        if (!exists) {
            FoodItem item;
            if ("DESSERT".equalsIgnoreCase(type)) {
                item = new Dessert();
            } else if ("BEVERAGE".equalsIgnoreCase(type)) {
                item = new Beverage();
            } else {
                item = new MainCourse();
            }
            item.setName(name);
            item.setDescription(desc);
            item.setBasePrice(price);
            item.setImageUrl(imgUrl);
            item.setCategory(category);
            item.setAvailable(true);
            item.setOwner(owner);
            foodItemRepository.save(item);
        }
    }
}
