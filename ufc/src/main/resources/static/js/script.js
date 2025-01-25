document.addEventListener('DOMContentLoaded', (event) => {
    // Initialize Lucide icons
    lucide.createIcons();

    // Set current year in footer
    document.getElementById('current-year').textContent = new Date().getFullYear();

    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });

    // Mobile menu toggle (if needed)
    const mobileMenuButton = document.getElementById('mobile-menu-button');
    const mobileMenu = document.getElementById('mobile-menu');

    if (mobileMenuButton && mobileMenu) {
        mobileMenuButton.addEventListener('click', () => {
            mobileMenu.classList.toggle('hidden');
        });
    }

    // Get all editable fields
    const editableFields = document.querySelectorAll('.editable-field');

    editableFields.forEach(field => {
        const input = field.querySelector('input, textarea');
        const editButton = field.querySelector('.btn-edit');
        let originalValue;

        editButton.addEventListener('click', function() {
            if (input.readOnly) {
                // Enter edit mode
                originalValue = input.value;
                input.readOnly = false;
                input.classList.remove('bg-gray-100');
                editButton.innerHTML = '<i data-lucide="check" class="h-4 w-4"></i>';
                lucide.createIcons();
            } else {
                // Save changes
                input.readOnly = true;
                input.classList.add('bg-gray-100');
                editButton.innerHTML = '<i data-lucide="edit-2" class="h-4 w-4"></i>';
                lucide.createIcons();

                // Here you would typically send the updated data to a server
                console.log(`Updated ${field.dataset.field}: ${input.value}`);
            }
        });

        // Add event listener for the Escape key to cancel editing
        input.addEventListener('keydown', function(event) {
            if (event.key === 'Escape' && !input.readOnly) {
                input.value = originalValue;
                input.readOnly = true;
                input.classList.add('bg-gray-100');
                editButton.innerHTML = '<i data-lucide="edit-2" class="h-4 w-4"></i>';
                lucide.createIcons();
            }
        });
    });
});