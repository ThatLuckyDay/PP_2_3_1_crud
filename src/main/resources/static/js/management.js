document.addEventListener('DOMContentLoaded', () => {
    // Инициализация модальных окон
    const editModal = new bootstrap.Modal(document.getElementById('editModal'));
    const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

    // Обработчик для формы редактирования
    document.getElementById('editForm')?.addEventListener('submit', async (e) => {
        e.preventDefault();

        const form = e.target;
        const formData = new FormData(form);
        const roles = [];

        // Собираем выбранные роли
        const adminChecked = document.getElementById('editRole_ADMIN').checked;
        const userChecked = document.getElementById('editRole_USER').checked;

        if (adminChecked) roles.push({ authority: 'ROLE_ADMIN' });
        if (userChecked) roles.push({ authority: 'ROLE_USER' });

        // Проверка, что выбрана хотя бы одна роль
        if (roles.length === 0) {
            showToast('Выберите хотя бы одну роль', 'warning');
            return;
        }

        const userData = {
            id: formData.get('id'),
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            age: formData.get('age'),
            email: formData.get('email'),
            password: formData.get('password'), // Пароль не обязателен при редактировании
            roles: roles
        };

        try {
            const response = await fetch(`/api/users/${userData.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData),
                credentials: 'include'
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Ошибка при обновлении пользователя');
            }

            editModal.hide();
            refreshUsersTable();
            showToast('Пользователь успешно обновлен', 'success');
        } catch (error) {
            console.error('Ошибка:', error);
            showToast(error.message || 'Ошибка при обновлении пользователя', 'danger');
        }
    });

    // Обработчик для формы удаления
    document.getElementById('deleteForm')?.addEventListener('submit', async (e) => {
        e.preventDefault();

        const userId = document.getElementById('deleteUserId').value;

        try {
            const response = await fetch(`/api/users/${userId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('Ошибка при удалении пользователя');
            }

            deleteModal.hide();
            refreshUsersTable(); // Обновляем таблицу без перезагрузки страницы
            showToast('Пользователь успешно удален', 'success');
        } catch (error) {
            console.error('Ошибка:', error);
            showToast('Ошибка при удалении пользователя', 'danger');
        }
    });

    // Функция для обновления таблицы пользователей
    function refreshUsersTable() {
        const activeTab = document.querySelector('.nav-tabs .nav-link.active');
        if (activeTab?.getAttribute('href') === '#usersTab') {
            // Если активна вкладка с таблицей пользователей, обновляем её
            const loadUsersFunction = window.loadUsers;
            if (typeof loadUsersFunction === 'function') {
                loadUsersFunction();
            }
        }
    }
});