document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/principal")
        .then(response => {
            if (!response.ok) {
                throw new Error("Ошибка загрузки данных пользователя");
            }
            return response.json();
        })
        .then(principal => {
            // Заполняем email
            const emailElement = document.getElementById("email-head");
            emailElement.textContent = principal.email;

            // Заполняем роли
            const rolesElement = document.getElementById("roles-head");
            if (principal.roles && principal.roles.length > 0) {
                const rolesText = principal.roles
                    .map(role => role.authority.replace("ROLE_", ""))
                    .join(" ");
                rolesElement.textContent = rolesText;
            } else {
                rolesElement.textContent = "No roles";
            }
        })
        .catch(error => {
            console.error("Ошибка при получении данных пользователя:", error);
            document.getElementById("email-head").textContent = "Ошибка загрузки";
        });
});