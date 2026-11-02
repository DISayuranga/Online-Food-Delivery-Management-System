/**
 * CraveBite Professional Notifications & Modals
 * Powered by SweetAlert2 with CraveBite modern food delivery UI branding
 */
(function() {
    // 1. Ensure SweetAlert2 is loaded
    function injectSweetAlert(callback) {
        if (window.Swal) {
            if (callback) callback();
            return;
        }

        // Add SweetAlert2 CSS
        if (!document.getElementById('swal2-style')) {
            const link = document.createElement('link');
            link.id = 'swal2-style';
            link.rel = 'stylesheet';
            link.href = 'https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css';
            document.head.appendChild(link);
        }

        // Add SweetAlert2 Script
        if (!document.getElementById('swal2-script')) {
            const script = document.createElement('script');
            script.id = 'swal2-script';
            script.src = 'https://cdn.jsdelivr.net/npm/sweetalert2@11';
            script.onload = () => {
                if (callback) callback();
            };
            document.head.appendChild(script);
        } else {
            // Already injected, wait for it
            const checkInterval = setInterval(() => {
                if (window.Swal) {
                    clearInterval(checkInterval);
                    if (callback) callback();
                }
            }, 50);
        }
    }

    // Initialize SweetAlert custom themes and expose Notify object
    function initNotify() {
        const BrandSwal = window.Swal.mixin({
            customClass: {
                popup: 'rounded-3xl shadow-2xl border border-gray-100 p-6 font-sans',
                title: 'text-xl font-black text-gray-900',
                htmlContainer: 'text-sm text-gray-600 leading-relaxed',
                confirmButton: 'bg-orange-500 hover:bg-orange-600 text-white font-bold py-3 px-6 rounded-2xl text-sm transition shadow-lg shadow-orange-500/25 mx-1.5 cursor-pointer',
                cancelButton: 'bg-gray-100 hover:bg-gray-200 text-gray-700 font-bold py-3 px-6 rounded-2xl text-sm transition mx-1.5 cursor-pointer',
                denyButton: 'bg-red-500 hover:bg-red-600 text-white font-bold py-3 px-6 rounded-2xl text-sm transition mx-1.5 cursor-pointer'
            },
            buttonsStyling: false
        });

        const Toast = window.Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            customClass: {
                popup: 'rounded-2xl shadow-xl border border-gray-100 font-sans p-3.5 bg-white/95 backdrop-blur',
                title: 'text-sm font-bold text-gray-900'
            },
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', window.Swal.stopTimer);
                toast.addEventListener('mouseleave', window.Swal.resumeTimer);
            }
        });

        window.Notify = {
            toast: (title, icon = 'success') => {
                return Toast.fire({
                    icon: icon,
                    title: title
                });
            },

            success: (title, text = '') => {
                return BrandSwal.fire({
                    icon: 'success',
                    title: title,
                    text: text,
                    confirmButtonText: 'Great!'
                });
            },

            error: (title, text = '') => {
                return BrandSwal.fire({
                    icon: 'error',
                    title: title,
                    text: text,
                    confirmButtonText: 'Understood'
                });
            },

            warning: (title, text = '') => {
                return BrandSwal.fire({
                    icon: 'warning',
                    title: title,
                    text: text,
                    confirmButtonText: 'Okay'
                });
            },

            info: (title, text = '') => {
                return BrandSwal.fire({
                    icon: 'info',
                    title: title,
                    text: text,
                    confirmButtonText: 'Got It'
                });
            },

            confirm: (title, text = '', confirmBtnText = 'Yes, Proceed', cancelBtnText = 'Cancel') => {
                return BrandSwal.fire({
                    icon: 'question',
                    title: title,
                    text: text,
                    showCancelButton: true,
                    confirmButtonText: confirmBtnText,
                    cancelButtonText: cancelBtnText,
                    reverseButtons: true
                });
            },

            /**
             * Specialized Checkout Confirmation Dialog showing delivery fee breakdown
             */
            confirmCheckout: function(subTotal, deliveryFee, total, onProceed) {
                return BrandSwal.fire({
                    title: 'Confirm Order & Delivery',
                    icon: 'info',
                    html: `
                        <div class="mt-3 text-left bg-orange-50/60 p-4 rounded-2xl border border-orange-100 text-sm space-y-2.5">
                            <div class="flex justify-between text-gray-600">
                                <span>Food Subtotal</span>
                                <span class="font-bold text-gray-900">Rs. ${Number(subTotal).toFixed(2)}</span>
                            </div>
                            <div class="flex justify-between text-orange-700 font-semibold">
                                <span class="flex items-center gap-1.5">🛵 Islandwide Delivery Fee</span>
                                <span class="font-bold">+ Rs. ${Number(deliveryFee).toFixed(2)}</span>
                            </div>
                            <div class="border-t border-orange-200/80 pt-2 flex justify-between items-center text-base font-black text-gray-900">
                                <span>Total Payable</span>
                                <span class="text-orange-600 text-lg">Rs. ${Number(total).toFixed(2)}</span>
                            </div>
                        </div>
                        <p class="text-xs text-gray-500 mt-3 text-center">Ready to proceed to delivery details and payment?</p>
                    `,
                    showCancelButton: true,
                    confirmButtonText: 'Proceed to Payment ➔',
                    cancelButtonText: 'Review Cart',
                    reverseButtons: true
                }).then(result => {
                    if (result.isConfirmed && typeof onProceed === 'function') {
                        onProceed();
                    }
                    return result;
                });
            }
        };

        // Override standard window.alert with modern modal
        window.alert = function(message) {
            try {
                if (window.Swal) {
                    let icon = 'info';
                    const msg = String(message || '');
                    if (msg.toLowerCase().includes('success') || msg.toLowerCase().includes('added')) icon = 'success';
                    else if (msg.toLowerCase().includes('error') || msg.toLowerCase().includes('fail') || msg.toLowerCase().includes('invalid')) icon = 'error';
                    else if (msg.toLowerCase().includes('warning') || msg.toLowerCase().includes('please')) icon = 'warning';

                    BrandSwal.fire({
                        title: 'Notification',
                        text: msg,
                        icon: icon,
                        confirmButtonText: 'OK'
                    });
                    return;
                }
            } catch (e) {
                console.warn('Custom alert failed, fallback', e);
            }
            console.log("Alert:", message);
        };
    }

    // Auto-inject and setup immediately
    injectSweetAlert(initNotify);
})();
