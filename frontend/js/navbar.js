// CraveBite Dynamic Role-Based Navbar Component
(function() {
    function renderNavbar() {
        const username = localStorage.getItem('username');
        const role = (localStorage.getItem('role') || '').toUpperCase();

        const navElement = document.getElementById('cravebite-nav') || document.querySelector('nav');
        if (!navElement) return;

        let leftLinks = '';
        let rightLinks = '';

        if (!username) {
            // Guest Navigation
            leftLinks = `
                <a href="/index.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Home</a>
                <a href="/about.html" class="hover:text-orange-500 font-bold text-gray-700 transition">About Us</a>
                <a href="/products-list.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Food</a>
                <a href="/contact.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Contact Us</a>
            `;
            rightLinks = `
                <a href="/login.html" class="text-gray-700 hover:text-orange-500 font-bold px-3 py-2 text-sm transition">Log in</a>
                <a href="/register.html" class="bg-gray-900 text-white px-5 py-2.5 rounded-full hover:bg-orange-500 transition-colors shadow font-bold text-sm">Sign Up (Customer)</a>
            `;
        } else if (role === 'CUSTOMER') {
            // Customer Side Navigation (Exact Order Requested):
            // 1. Home, 2. About us, 3. Food, 4. My Cart, 5. My Orders, 6. Contact us, 7. Profile & Settings
            leftLinks = `
                <a href="/index.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Home</a>
                <a href="/about.html" class="hover:text-orange-500 font-bold text-gray-700 transition">About Us</a>
                <a href="/products-list.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Food</a>
                <a href="/cart-view.html" class="hover:text-orange-500 font-bold text-gray-700 transition">My Cart</a>
                <a href="/order-history.html" class="hover:text-orange-500 font-bold text-gray-700 transition">My Orders</a>
                <a href="/contact.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Contact Us</a>
                <a href="/profile.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Profile & Settings</a>
            `;
            rightLinks = `
                <a href="/cart-view.html" class="bg-orange-100 text-orange-600 px-4 py-2 rounded-full font-bold hover:bg-orange-200 transition text-sm flex items-center gap-1 shadow-sm">🛒 Cart</a>
                <span class="text-xs bg-gray-100 text-gray-700 px-3 py-1.5 rounded-full font-bold">@${username}</span>
                <button onclick="craveBiteLogout()" class="bg-gray-100 text-gray-600 px-4 py-2 rounded-full text-xs font-bold hover:bg-red-50 hover:text-red-500 transition">Log out</button>
            `;
        } else if (role === 'RESTAURANT_OWNER') {
            // Restaurant Side Navigation:
            // 1. Dashboard, 2. Food Items, 3. Orders, 4. Customer Reviews, 5. Sales & Reports
            leftLinks = `
                <a href="/restaurant-orders.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Dashboard</a>
                <a href="/product-add.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Food Items</a>
                <a href="/restaurant-orders.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Orders</a>
                <a href="/restaurant-reviews.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Customer Reviews</a>
                <a href="/restaurant-sales.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Sales & Reports</a>
            `;
            rightLinks = `
                <a href="/profile.html" class="text-gray-700 hover:text-orange-500 font-bold text-sm transition">👤 Profile</a>
                <span class="text-xs bg-amber-100 text-amber-800 px-3 py-1.5 rounded-full font-bold">Restaurant</span>
                <button onclick="craveBiteLogout()" class="bg-gray-100 text-gray-600 px-4 py-2 rounded-full text-xs font-bold hover:bg-red-50 hover:text-red-500 transition">Log out</button>
            `;
        } else if (role === 'ADMIN') {
            // Admin Side Navigation (Exact Order Requested):
            // 1. Dashboard & Analytics, 2. Manage Customers, 3. Manage Restaurants, 4. Manage Riders, 5. Manage Orders & Deliveries
            leftLinks = `
                <a href="/admin-dashboard.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Dashboard & Analytics</a>
                <a href="/admin-dashboard.html?tab=customers" class="hover:text-orange-500 font-bold text-gray-700 transition">Manage Customers</a>
                <a href="/admin-dashboard.html?tab=restaurants" class="hover:text-orange-500 font-bold text-gray-700 transition">Manage Restaurants</a>
                <a href="/admin-dashboard.html?tab=riders" class="hover:text-orange-500 font-bold text-gray-700 transition">Manage Riders</a>
                <a href="/admin-dashboard.html?tab=orders" class="hover:text-orange-500 font-bold text-gray-700 transition">Manage Orders & Deliveries</a>
            `;
            rightLinks = `
                <span class="bg-purple-100 text-purple-700 px-3 py-1.5 rounded-full text-xs font-extrabold uppercase tracking-wider">Super Admin</span>
                <a href="/profile.html" class="text-gray-700 hover:text-orange-500 font-bold text-sm transition">Profile</a>
                <button onclick="craveBiteLogout()" class="bg-gray-100 text-gray-600 px-4 py-2 rounded-full text-xs font-bold hover:bg-red-50 hover:text-red-500 transition">Log out</button>
            `;
        } else if (role === 'RIDER') {
            // Rider Side Navigation
            leftLinks = `
                <a href="/rider-orders.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Rider Dashboard</a>
                <a href="/rider-orders.html#active-deliveries" class="hover:text-orange-500 font-bold text-gray-700 transition">Deliveries</a>
                <a href="/about.html" class="hover:text-orange-500 font-bold text-gray-700 transition">About Us</a>
            `;
            rightLinks = `
                <a href="http://localhost:8080/api/orders/reports/rider-deliveries?username=${encodeURIComponent(username)}" target="_blank" class="bg-orange-500 text-white px-4 py-2 rounded-full text-xs font-bold hover:bg-orange-600 transition shadow">📄 Download Report</a>
                <a href="/profile.html" class="text-gray-700 hover:text-orange-500 font-bold text-sm transition">👤 Rider Profile</a>
                <button onclick="craveBiteLogout()" class="bg-gray-100 text-gray-600 px-4 py-2 rounded-full text-xs font-bold hover:bg-red-50 hover:text-red-500 transition">Log out</button>
            `;
        } else {
            // Fallback
            leftLinks = `
                <a href="/index.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Home</a>
                <a href="/about.html" class="hover:text-orange-500 font-bold text-gray-700 transition">About Us</a>
                <a href="/products-list.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Food</a>
                <a href="/contact.html" class="hover:text-orange-500 font-bold text-gray-700 transition">Contact Us</a>
            `;
            rightLinks = `
                <a href="/profile.html" class="text-gray-700 hover:text-orange-500 font-bold text-sm transition">Dashboard (@${username})</a>
                <button onclick="craveBiteLogout()" class="bg-gray-100 text-gray-600 px-4 py-2 rounded-full text-xs font-bold hover:bg-red-50 hover:text-red-500 transition">Log out</button>
            `;
        }

        navElement.className = "bg-white shadow-sm sticky top-0 z-50 border-b border-gray-100";
        navElement.innerHTML = `
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div class="flex justify-between h-20 items-center">
                    <div class="flex items-center gap-8">
                        <a href="/index.html" class="flex items-center gap-2">
                            <div class="bg-orange-500 p-2 rounded-xl">
                                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path></svg>
                            </div>
                            <span class="text-2xl font-black text-gray-900 tracking-tight">CraveBite<span class="text-orange-500">.</span></span>
                        </a>
                        <div class="hidden xl:flex items-center space-x-6 text-sm">
                            ${leftLinks}
                        </div>
                    </div>
                    <div class="flex items-center space-x-4">
                        ${rightLinks}
                    </div>
                </div>
                <!-- Mobile / Tablet Sub-bar -->
                <div class="xl:hidden flex flex-wrap gap-4 py-2 border-t border-gray-100 text-xs">
                    ${leftLinks}
                </div>
            </div>
        `;
    }

    window.craveBiteLogout = function() {
        localStorage.removeItem('username');
        localStorage.removeItem('role');
        localStorage.removeItem('fullName');
        window.location.href = '/login.html';
    };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', renderNavbar);
    } else {
        renderNavbar();
    }
})();
