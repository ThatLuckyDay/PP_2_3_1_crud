document.addEventListener("DOMContentLoaded", function () {
    const tableBody = document.querySelector("#selfTable tbody");

    fetch("/api/principal")
        .then(response => {
            if (!response.ok) {
                throw new Error("Ошибка при получении данных");
            }
            return response.json();
        })
        .then(user => {
            const row = document.createElement("tr");

            // ID
            const idCell = document.createElement("td");
            idCell.textContent = user.id;
            row.appendChild(idCell);

            // First Name
            const firstNameCell = document.createElement("td");
            firstNameCell.textContent = user.firstName;
            row.appendChild(firstNameCell);

            // Last Name
            const lastNameCell = document.createElement("td");
            lastNameCell.textContent = user.lastName;
            row.appendChild(lastNameCell);

            // Age
            const ageCell = document.createElement("td");
            ageCell.textContent = user.age;
            row.appendChild(ageCell);

            // Email
            const emailCell = document.createElement("td");
            emailCell.textContent = user.email;
            row.appendChild(emailCell);

            // Role
            const roleCell = document.createElement("td");
            roleCell.textContent = user.roles.map(r => r.authority.replace("ROLE_", "")).join(", ");
            row.appendChild(roleCell);

            tableBody.appendChild(row);
        })
        .catch(error => {
            console.error("Ошибка загрузки данных:", error);
            tableBody.innerHTML = "<tr><td colspan='6' class='text-center'>Ошибка загрузки данных</td></tr>";
        });
});