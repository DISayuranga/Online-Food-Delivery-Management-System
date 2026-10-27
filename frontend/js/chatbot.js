/**
 * CraveBot - AI Virtual Assistant for CraveBite Platform
 * Automatically loaded across all pages via navbar.js
 */

(function initCraveBot() {
    if (document.getElementById('cravebot-container')) return;

    // Inject CraveBot HTML into body
    const botHtml = `
    <div id="cravebot-container" class="fixed bottom-6 right-6 z-50 font-sans">
        <!-- Floating Launcher Button -->
        <button id="cravebot-launcher" onclick="toggleCraveBot()" 
                class="relative w-14 h-14 bg-gradient-to-tr from-orange-600 to-orange-400 text-white rounded-full shadow-2xl flex items-center justify-center hover:scale-110 active:scale-95 transition-all duration-300 ring-4 ring-orange-100 group"
                title="Chat with CraveBot AI">
            <span class="text-2xl group-hover:rotate-12 transition-transform">🤖</span>
            <span class="absolute -top-1 -right-1 flex h-3.5 w-3.5">
                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
                <span class="relative inline-flex rounded-full h-3.5 w-3.5 bg-green-500 border-2 border-white"></span>
            </span>
        </button>

        <!-- Chat Modal Window -->
        <div id="cravebot-window" class="hidden absolute bottom-16 right-0 w-[360px] sm:w-[400px] h-[520px] bg-white rounded-3xl shadow-2xl border border-gray-100 flex flex-col overflow-hidden animate-in fade-in slide-in-from-bottom-5 duration-200">
            <!-- Header -->
            <div class="bg-gray-900 text-white p-4 flex items-center justify-between">
                <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-2xl bg-orange-500 flex items-center justify-center text-xl shadow">
                        🤖
                    </div>
                    <div>
                        <h4 class="font-black text-sm tracking-tight flex items-center gap-2">
                            CraveBot AI
                            <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-green-500/20 text-green-400 border border-green-500/30">Online</span>
                        </h4>
                        <p class="text-[11px] text-gray-400">Virtual Food & Delivery Assistant</p>
                    </div>
                </div>
                <button onclick="toggleCraveBot()" class="w-8 h-8 rounded-full bg-gray-800 hover:bg-gray-700 text-gray-400 hover:text-white flex items-center justify-center text-lg transition">
                    &times;
                </button>
            </div>

            <!-- Messages Area -->
            <div id="cravebot-messages" class="flex-grow p-4 overflow-y-auto space-y-3 bg-gray-50/50 text-xs">
                <!-- Welcome Message -->
                <div class="flex gap-2.5 items-start">
                    <div class="w-7 h-7 rounded-full bg-orange-500 text-white flex items-center justify-center text-sm shrink-0">🤖</div>
                    <div class="bg-white p-3.5 rounded-2xl rounded-tl-none border border-gray-100 shadow-xs text-gray-800 leading-relaxed space-y-2">
                        <p>👋 Hello! I am <strong>CraveBot</strong>, your personal food ordering and delivery assistant.</p>
                        <p>How can I help you today? You can choose a quick question below or type your question:</p>
                    </div>
                </div>

                <!-- Quick Suggestion Chips -->
                <div id="cravebot-quick-chips" class="flex flex-wrap gap-1.5 pt-1 pl-9">
                    <button onclick="askBotQuestion('What categories are available?')" class="bg-white hover:bg-orange-50 text-gray-700 hover:text-orange-600 border border-gray-200 hover:border-orange-300 px-3 py-1.5 rounded-full text-[11px] font-bold transition shadow-2xs">
                        🍔 Categories
                    </button>
                    <button onclick="askBotQuestion('How do I schedule an order for tomorrow?')" class="bg-white hover:bg-orange-50 text-gray-700 hover:text-orange-600 border border-gray-200 hover:border-orange-300 px-3 py-1.5 rounded-full text-[11px] font-bold transition shadow-2xs">
                        ⏰ Schedule Order
                    </button>
                    <button onclick="askBotQuestion('Track my active order')" class="bg-white hover:bg-orange-50 text-gray-700 hover:text-orange-600 border border-gray-200 hover:border-orange-300 px-3 py-1.5 rounded-full text-[11px] font-bold transition shadow-2xs">
                        🛵 Track Order
                    </button>
                    <button onclick="askBotQuestion('How do I contact restaurant or rider?')" class="bg-white hover:bg-orange-50 text-gray-700 hover:text-orange-600 border border-gray-200 hover:border-orange-300 px-3 py-1.5 rounded-full text-[11px] font-bold transition shadow-2xs">
                        ✉️ Contact & Reply
                    </button>
                    <button onclick="askBotQuestion('How do I save payment cards and addresses?')" class="bg-white hover:bg-orange-50 text-gray-700 hover:text-orange-600 border border-gray-200 hover:border-orange-300 px-3 py-1.5 rounded-full text-[11px] font-bold transition shadow-2xs">
                        💳 Cards & Location
                    </button>
                </div>
            </div>

            <!-- Input Box -->
            <form id="cravebot-form" onsubmit="handleBotSubmit(event)" class="p-3 bg-white border-t border-gray-100 flex gap-2 items-center">
                <input type="text" id="cravebot-input" placeholder="Ask CraveBot anything..." 
                       class="flex-grow px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-2 focus:ring-orange-500">
                <button type="submit" class="bg-orange-500 hover:bg-orange-600 text-white w-10 h-10 rounded-xl flex items-center justify-center font-bold text-sm shadow transition shrink-0">
                    ➤
                </button>
            </form>
        </div>
    </div>
    `;

    document.body.insertAdjacentHTML('beforeend', botHtml);
})();

function toggleCraveBot() {
    const win = document.getElementById('cravebot-window');
    if (!win) return;
    const isHidden = win.classList.contains('hidden');
    if (isHidden) {
        win.classList.remove('hidden');
        document.getElementById('cravebot-input')?.focus();
    } else {
        win.classList.add('hidden');
    }
}

function askBotQuestion(question) {
    const input = document.getElementById('cravebot-input');
    if (input) {
        input.value = question;
        handleBotSubmit(new Event('submit'));
    }
}

function handleBotSubmit(e) {
    if (e && e.preventDefault) e.preventDefault();
    const input = document.getElementById('cravebot-input');
    const msgArea = document.getElementById('cravebot-messages');
    if (!input || !msgArea) return;

    const userText = input.value.trim();
    if (!userText) return;

    // Append User Message
    msgArea.insertAdjacentHTML('beforeend', `
        <div class="flex gap-2.5 items-start justify-end">
            <div class="bg-orange-500 text-white p-3 rounded-2xl rounded-tr-none text-xs font-semibold max-w-[80%] leading-relaxed shadow-sm">
                ${escapeHtml(userText)}
            </div>
            <div class="w-7 h-7 rounded-full bg-gray-900 text-white flex items-center justify-center text-xs shrink-0 font-bold">👤</div>
        </div>
    `);

    input.value = '';
    msgArea.scrollTop = msgArea.scrollHeight;

    // Bot Typing Indicator
    const typingId = 'typing-' + Date.now();
    msgArea.insertAdjacentHTML('beforeend', `
        <div id="${typingId}" class="flex gap-2.5 items-start">
            <div class="w-7 h-7 rounded-full bg-orange-500 text-white flex items-center justify-center text-sm shrink-0">🤖</div>
            <div class="bg-white p-3 rounded-2xl rounded-tl-none border border-gray-100 text-gray-400 italic text-[11px] animate-pulse">
                Thinking...
            </div>
        </div>
    `);
    msgArea.scrollTop = msgArea.scrollHeight;

    setTimeout(() => {
        const typingElem = document.getElementById(typingId);
        if (typingElem) typingElem.remove();

        const botReply = generateBotResponse(userText);
        msgArea.insertAdjacentHTML('beforeend', `
            <div class="flex gap-2.5 items-start">
                <div class="w-7 h-7 rounded-full bg-orange-500 text-white flex items-center justify-center text-sm shrink-0">🤖</div>
                <div class="bg-white p-3.5 rounded-2xl rounded-tl-none border border-gray-100 shadow-xs text-gray-800 leading-relaxed text-xs space-y-2 max-w-[85%]">
                    ${botReply}
                </div>
            </div>
        `);
        msgArea.scrollTop = msgArea.scrollHeight;
    }, 400);
}

function generateBotResponse(input) {
    const q = input.toLowerCase();
    const username = localStorage.getItem('username');

    // 1. Order Scheduling
    if (q.includes('schedule') || q.includes('time') || q.includes('heta') || q.includes('tomorrow') || q.includes('later')) {
        return `
            <p><strong>⏰ Order Scheduling is easy on CraveBite:</strong></p>
            <ol class="list-decimal pl-4 space-y-1">
                <li>Add food items to <a href="/cart-view.html" class="text-orange-500 font-bold underline">Your Cart</a>.</li>
                <li>Proceed to <strong>Checkout</strong>.</li>
                <li>Under <em>Delivery Timing & Schedule</em>, select <strong>⏰ Schedule for Later</strong>.</li>
                <li>Pick your preferred <strong>Date</strong> (e.g. tomorrow) and <strong>Time</strong>.</li>
            </ol>
            <p class="text-gray-500 text-[11px]">The restaurant will see your schedule and prepare your meal right on time!</p>
        `;
    }

    // 2. Track Order / Status
    if (q.includes('track') || q.includes('status') || q.includes('order') || q.includes('where is')) {
        if (username) {
            return `
                <p>🛵 You can track your real-time active order and kitchen preparation progress here:</p>
                <div class="pt-1">
                    <a href="/order-tracking.html" class="inline-block bg-orange-500 text-white px-3 py-1.5 rounded-xl font-bold text-[11px] shadow-sm hover:bg-orange-600 transition">
                        View Order Tracking ➔
                    </a>
                </div>
                <p class="text-gray-500 text-[11px]">You can also check past orders in <a href="/order-history.html" class="underline font-bold">My Orders History</a>.</p>
            `;
        } else {
            return `
                <p>To track your orders, please <a href="/login.html" class="text-orange-500 font-bold underline">Log in</a> first. Then visit <a href="/order-tracking.html" class="text-orange-500 font-bold underline">Order Tracking</a>.</p>
            `;
        }
    }

    // 3. Categories / Food / Menu
    if (q.includes('category') || q.includes('categories') || q.includes('burger') || q.includes('pizza') || q.includes('lunch') || q.includes('dinner') || q.includes('juice') || q.includes('food') || q.includes('menu')) {
        return `
            <p>🍽️ <strong>Explore 11 Delicious Categories:</strong></p>
            <div class="flex flex-wrap gap-1 pt-1 text-[11px]">
                <span class="bg-gray-100 px-2 py-0.5 rounded">🍔 Burgers</span>
                <span class="bg-gray-100 px-2 py-0.5 rounded">🍕 Pizza</span>
                <span class="bg-gray-100 px-2 py-0.5 rounded">🍛 Lunch</span>
                <span class="bg-gray-100 px-2 py-0.5 rounded">🍲 Dinner</span>
                <span class="bg-gray-100 px-2 py-0.5 rounded">🧃 Juice</span>
                <span class="bg-gray-100 px-2 py-0.5 rounded">🍣 Sushi</span>
                <span class="bg-gray-100 px-2 py-0.5 rounded">🥗 Healthy</span>
                <span class="bg-gray-100 px-2 py-0.5 rounded">🍰 Desserts</span>
                <span class="bg-gray-100 px-2 py-0.5 rounded">🍳 Breakfast</span>
                <span class="bg-gray-100 px-2 py-0.5 rounded">🥤 Beverages</span>
                <span class="bg-gray-100 px-2 py-0.5 rounded">🥘 Main Course</span>
            </div>
            <div class="pt-2">
                <a href="/products-list.html" class="text-orange-500 font-bold underline">Browse All Dishes ➔</a>
            </div>
        `;
    }

    // 4. Contact / Message / Reply
    if (q.includes('contact') || q.includes('message') || q.includes('reply') || q.includes('rider') || q.includes('owner') || q.includes('admin')) {
        return `
            <p>✉️ <strong>Direct Contact & Reply System:</strong></p>
            <p>You can send messages directly to:</p>
            <ul class="list-disc pl-4 space-y-0.5">
                <li>🛡️ Platform Support & Admin</li>
                <li>🏪 Any Registered Restaurant Owner</li>
                <li>🛵 Delivery Riders</li>
            </ul>
            <p class="pt-1">Visit our <a href="/contact.html" class="text-orange-500 font-bold underline">Contact Us Page</a> to submit your inquiry. When they reply, you can view the response with the responder's identity!</p>
        `;
    }

    // 5. Payment Cards & Addresses
    if (q.includes('card') || q.includes('payment') || q.includes('address') || q.includes('location') || q.includes('map') || q.includes('profile')) {
        return `
            <p>💳 <strong>Cards & Delivery Locations:</strong></p>
            <p>In your <a href="/profile.html" class="text-orange-500 font-bold underline">Profile & Settings</a>, you can:</p>
            <ul class="list-disc pl-4 space-y-0.5">
                <li>Save Credit / Debit cards with realistic SVG display.</li>
                <li>Set up drop-off locations with <strong>Interactive Google Maps</strong> picker!</li>
            </ul>
        `;
    }

    // 6. Greetings
    if (q.includes('hi') || q.includes('hello') || q.includes('hey') || q.includes('ayubowan') || q.includes('kohomada')) {
        return `
            <p>Hello there! 😊 How can I help you today? Ask me about our menu, restaurants, scheduled delivery, or order tracking!</p>
        `;
    }

    // Fallback
    return `
        <p>Thanks for asking! I'm here to help you order, schedule deliveries, and track orders.</p>
        <p>You can check out our <a href="/products-list.html" class="text-orange-500 font-bold underline">Dishes</a>, <a href="/contact.html" class="text-orange-500 font-bold underline">Contact Us</a>, or view <a href="/order-tracking.html" class="text-orange-500 font-bold underline">Order Tracking</a>.</p>
    `;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.innerText = text;
    return div.innerHTML;
}
