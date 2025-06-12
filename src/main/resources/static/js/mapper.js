document.addEventListener('DOMContentLoaded', function() {
    // Edit Modal
    const editModal = document.getElementById('editModal');
    if (editModal) {
        editModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;

            // Получаем данные из атрибутов кнопки
            document.getElementById('editUserId').value = button.getAttribute('data-bs-user-id');
            document.getElementById('editFirstName').value = button.getAttribute('data-bs-first-name');
            document.getElementById('editLastName').value = button.getAttribute('data-bs-last-name');
            document.getElementById('editAge').value = button.getAttribute('data-bs-age');
            document.getElementById('editEmail').value = button.getAttribute('data-bs-email');

            // Обработка ролей
            const roles = button.getAttribute('data-bs-roles').split(',');
            document.querySelectorAll('#editModal input[name="roles"]').forEach(checkbox => {
                const roleValue = checkbox.value;
                checkbox.checked = roles.some(role => role.includes(roleValue));
            });
        });
    }

    // Delete Modal
    const deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;

            // Получаем данные из атрибутов кнопки
            document.getElementById('deleteUserId').value = button.getAttribute('data-bs-user-id');
            document.getElementById('deleteFirstName').value = button.getAttribute('data-bs-first-name');
            document.getElementById('deleteLastName').value = button.getAttribute('data-bs-last-name');
            document.getElementById('deleteAge').value = button.getAttribute('data-bs-age');
            document.getElementById('deleteEmail').value = button.getAttribute('data-bs-email');

            // Обработка ролей
            const roles = button.getAttribute('data-bs-roles').split(',');
            document.querySelectorAll('#deleteModal input[name="roles"]').forEach(checkbox => {
                const roleValue = checkbox.value;
                checkbox.checked = roles.some(role => role.includes(roleValue));
            });
        });
    }
});