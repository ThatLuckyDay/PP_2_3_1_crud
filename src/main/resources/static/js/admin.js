window.loadUsers = loadUsers;

document.addEventListener("DOMContentLoaded", function () {
    const targetNode = document.getElementById('pagesContent');
    if (!targetNode) return;

    let isLoaded = false;

    const config = { childList: true, subtree: true };

    const callback = function(mutationsList, observer) {
        if (isLoaded) return;

        for (let mutation of mutationsList) {
            if (mutation.type === 'childList') {
                if (document.getElementById('usersTable')) {
                    setupTabListeners();
                    checkInitialLoad();
                    isLoaded = true;
                    observer.disconnect(); // останавливаем наблюдение
                }
            }
        }
    };

    const observer = new MutationObserver(callback);
    observer.observe(targetNode, config);

    // Попробуем один раз проверить без ожидания изменений
    if (document.getElementById('usersTable')) {
        setupTabListeners();
        checkInitialLoad();
        isLoaded = true;
        observer.disconnect();
    }
});

function setupTabListeners() {
    const tabEls = document.querySelectorAll('[data-bs-toggle="tab"]');
    if (!tabEls.length) return;

    tabEls.forEach(tab => {
        if (!tab.dataset.listenerAttached) {
            tab.dataset.listenerAttached = "true"; // помечаем, что обработчик добавлен
            tab.addEventListener('shown.bs.tab', handleTabShow);
        }
    });
}

function handleTabShow(e) {
    const target = e.target.getAttribute('href');

    if (target === '#usersTab') {
        loadUsers();
    } else if (target === '#createUserTab') {
        loadCreateForm();
    }
}

function checkInitialLoad() {
    const activeTab = document.querySelector('.nav-tabs .nav-link.active');
    const target = activeTab?.getAttribute('href');

    if (target === '#usersTab') {
        loadUsers();
    } else if (target === '#createUserTab') {
        loadCreateForm();
    }
}

function loadUsers() {
    const table = document.getElementById('usersTable');
    if (!table) return;

    const tbody = table.querySelector('tbody');
    fetch('/api/users')
        .then(res => res.json())
        .then(users => {
            let rowsHTML = '';

            users.forEach(user => {
                const rolesStr = user.roles.map(r => r.authority.replace('ROLE_', '')).join(', ');

                rowsHTML += `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.firstName}</td>
                        <td>${user.lastName}</td>
                        <td>${user.age}</td>
                        <td>${user.email}</td>
                        <td>${rolesStr}</td>
                        <td class="action-buttons">
                            <button type="button"
                                    class="btn btn-sm btn-primary"
                                    data-bs-toggle="modal"
                                    data-bs-target="#editModal"
                                    data-bs-user-id="${user.id}"
                                    data-bs-first-name="${user.firstName}"
                                    data-bs-last-name="${user.lastName}"
                                    data-bs-age="${user.age}"
                                    data-bs-email="${user.email}"
                                    data-bs-roles="${rolesStr}">
                                Edit
                            </button>
                        </td>
                        <td class="action-buttons">
                            <button type="button"
                                    class="btn btn-sm btn-danger"
                                    data-bs-toggle="modal"
                                    data-bs-target="#deleteModal"
                                    data-bs-user-id="${user.id}"
                                    data-bs-first-name="${user.firstName}"
                                    data-bs-last-name="${user.lastName}"
                                    data-bs-age="${user.age}"
                                    data-bs-email="${user.email}"
                                    data-bs-roles="${rolesStr}">
                                Delete
                            </button>
                        </td>
                    </tr>
                `;
            });

            tbody.innerHTML = rowsHTML;
            setupModalData();
        })
        .catch(err => console.error('Ошибка при загрузке пользователей:', err));
}

function loadCreateForm() {
    const formContainer = document.getElementById('createUserForm');
    if (!formContainer) return;

    formContainer.innerHTML = `
        <div class="mb-3 w-25 mx-auto">
            <label class="form-label">First Name</label>
            <input type="text" class="form-control" name="firstName" required/>
        </div>
        <div class="mb-3 w-25 mx-auto">
            <label class="form-label">Last Name</label>
            <input type="text" class="form-control" name="lastName" required/>
        </div>
        <div class="mb-3 w-25 mx-auto">
            <label class="form-label">Age</label>
            <input type="number" class="form-control" name="age" required/>
        </div>
        <div class="mb-3 w-25 mx-auto">
            <label class="form-label">Email</label>
            <input type="email" class="form-control" name="email" required/>
        </div>
        <div class="mb-3 w-25 mx-auto">
            <label class="form-label">Password</label>
            <input type="password" class="form-control" name="password" required/>
        </div>
        <div class="mb-3 w-25 mx-auto">
            <label class="form-label">Role</label>
            <div class="form-check text-start fw-normal">
                <input class="form-check-input" type="checkbox" id="roleAdmin" name="roles" value="ADMIN">
                <label class="form-check-label" for="roleAdmin">ADMIN</label>
            </div>
            <div class="form-check text-start fw-normal">
                <input class="form-check-input" type="checkbox" id="roleUser" name="roles" value="USER">
                <label class="form-check-label" for="roleUser">USER</label>
            </div>
        </div>
        <div class="d-flex justify-content-center">
            <button type="submit" class="btn btn-success">Add new user</button>
        </div>
    `;

    formContainer.onsubmit = function (e) {
        e.preventDefault();

        const formData = new FormData(formContainer);
        const roles = [];

        // Собираем выбранные роли
        if (formContainer.querySelector('#roleAdmin').checked) {
            roles.push({ authority: 'ROLE_ADMIN' });
        }
        if (formContainer.querySelector('#roleUser').checked) {
            roles.push({ authority: 'ROLE_USER' });
        }

        const newUser = {
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            age: Number(formData.get('age')),
            email: formData.get('email'),
            password: formData.get('password'),
            roles: roles.length > 0 ? roles : [{ authority: 'ROLE_USER' }] // По умолчанию USER, если ничего не выбрано
        };

        fetch('/api/users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newUser)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка при создании пользователя');
            }
            return response.json();
        })
        .then(() => {
            const usersTabLink = document.querySelector('[href="#usersTab"]');
            if (usersTabLink) {
                const tab = new bootstrap.Tab(usersTabLink);
                tab.show();
            }
            loadUsers();
        })
        .catch(err => {
            console.error('Ошибка при создании пользователя:', err);
            alert('Ошибка при создании пользователя: ' + err.message);
        });
    };
}


function setupModalData() {
    const editModal = document.getElementById('editModal');
    if (editModal) {
        editModal.addEventListener('show.bs.modal', event => {
            const button = event.relatedTarget;
            const userId = button.getAttribute('data-bs-user-id');
            const firstName = button.getAttribute('data-bs-first-name');
            const lastName = button.getAttribute('data-bs-last-name');
            const age = button.getAttribute('data-bs-age');
            const email = button.getAttribute('data-bs-email');
            const roles = button.getAttribute('data-bs-roles');

            editModal.querySelector('#editUserId').value = userId;
            editModal.querySelector('#editFirstName').value = firstName;
            editModal.querySelector('#editLastName').value = lastName;
            editModal.querySelector('#editAge').value = age;
            editModal.querySelector('#editEmail').value = email;

            const isAdmin = roles.includes('ADMIN');
            const isUser = roles.includes('USER');

            editModal.querySelector('#editRole_ADMIN').checked = isAdmin;
            editModal.querySelector('#editRole_USER').checked = isUser;
        });
    }

    const deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', event => {
            const button = event.relatedTarget;
            const userId = button.getAttribute('data-bs-user-id');
            const firstName = button.getAttribute('data-bs-first-name');
            const lastName = button.getAttribute('data-bs-last-name');
            const age = button.getAttribute('data-bs-age');
            const email = button.getAttribute('data-bs-email');
            const roles = button.getAttribute('data-bs-roles');

            deleteModal.querySelector('#deleteUserId').value = userId;
            deleteModal.querySelector('#deleteFirstName').value = firstName;
            deleteModal.querySelector('#deleteLastName').value = lastName;
            deleteModal.querySelector('#deleteAge').value = age;
            deleteModal.querySelector('#deleteEmail').value = email;

            const isAdmin = roles.includes('ADMIN');
            const isUser = roles.includes('USER');

            deleteModal.querySelector('#deleteRole_ADMIN').checked = isAdmin;
            deleteModal.querySelector('#deleteRole_USER').checked = isUser;
        });
    }
}