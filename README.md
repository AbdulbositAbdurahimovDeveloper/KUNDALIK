# KUNDALIK - Your Smart Personal Assistant

![Project Banner](./assets/kundalik-banner.png)

**KUNDALIK** is a comprehensive, multi-platform personal assistant designed to streamline your daily life. It combines a
powerful REST API backend with a user-friendly Telegram Bot, allowing you to manage your notes, reminders, and personal
finances from anywhere.

This project is built with a professional, scalable architecture, making it an excellent reference for developers
interested in modern backend engineering with Java and Spring Boot.

---

## ✨ Features

KUNDALIK is more than just a to-do list; it's a complete life management tool.

### For Everyone (Anonymous Users)

* **🌦 Real-time Weather Forecasts:** Get current weather and 3-day forecasts for any city.
* **🕌 Daily Prayer Times:** Access accurate prayer schedules for any location.
* **💹 Currency Exchange Rates:** Stay updated with official currency rates.

### For Registered Users (Free Tier)

* **🗒️ Personal Notes:** A simple and secure place to jot down your ideas, thoughts, and lists.
* **⏰ Smart Reminders:** Set one-time or recurring reminders (daily, weekly, monthly) for important tasks.
* **🎂 Birthday Tracker:** Never forget a loved one's birthday again. Get automatic notifications in advance.
* **🤖 Automated Daily Briefings:** Configure the bot to send you personalized daily updates, such as your city's weather
  forecast or prayer times, at a time of your choice.
* **⚙️ Full Personalization:** Set your default city, preferred language, and other settings for a tailored experience.

### For Premium Users

* **💰 Personal Finance Manager ("Kashlok"):** A complete toolkit for managing your money.
    * **Multiple Accounts:** Track your finances across different sources (e.g., cash, bank cards).
    * **Income & Expense Tracking:** Log all your transactions with custom categories.
    * **Intelligent Reporting:** Get detailed reports and visual analytics to understand your spending habits.
    * **Custom Categories:** Create your own income and expense categories to fit your lifestyle.

---

## 🛠️ Tech Stack & Architecture

This project is built using a modern, robust technology stack, emphasizing clean code, scalability, and maintainability.

### Backend & Core

* **Language:** Java 17
* **Framework:** Spring Boot 3
* **Data Persistence:** Spring Data JPA (Hibernate)
* **Database:** PostgreSQL
* **Database Migrations:** Liquibase
* **API:** RESTful API with Spring Web MVC
* **Authentication & Authorization:** Spring Security with JWT

### Telegram Bot

* **Library:** [TelegramBots](https://github.com/rubenlagus/TelegramBots)
* **Communication:** Webhook-based for high performance and scalability.

### Other Key Technologies

* **Caching:** Spring Cache with Redis for performance optimization.
* **File Storage:** MinIO (S3-compatible object storage) for handling file uploads like profile pictures.
* **API Documentation:** Springdoc OpenAPI (Swagger UI) for interactive API documentation.
* **Code Generation:** Lombok & MapStruct to reduce boilerplate code.
* **Scheduling:** `cron-utils` for parsing and managing recurring tasks.

### Architectural Highlights

* **Modular Design:** The application is logically separated into `site` (REST API) and `telegram` (Bot Logic) modules.
* **Database First with Migrations:** The database schema is managed entirely through Liquibase, ensuring consistency
  across all environments.
* **Soft Deletion:** Implemented across most entities to prevent accidental data loss and maintain data integrity.
* **Internationalization (i18n):** The bot is designed to be multi-lingual from the ground up.

---

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* JDK 17 or higher
* Maven 3.6+
* PostgreSQL 14+
* An active Redis instance
* An active MinIO instance

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/AbdulbositAbdurahimovDeveloper/KUNDALIK.git
   cd KUNDALIK
   ```

2. **Configure Environment Variables:**
   Create a `.env` file in the root directory and populate it with your database credentials, JWT secret, MinIO keys,
   and Telegram Bot token. See `.env.example` for a template.

3. **Setup the Database:**
    * Create a new PostgreSQL database.
    * Update the `spring.datasource.*` properties in `application.yaml` or your `.env` file.

4. **Run the application:**
   The application uses `spring-dotenv` to load environment variables. You can run it directly from your IDE or using
   Maven:
   ```bash
   mvn spring-boot:run
   ```
   Liquibase will automatically create and migrate the database schema on the first startup.

5. **Access the API:**
    * **Swagger UI:** `http://localhost:8080/swagger-ui.html`

---

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also
simply open an issue with the tag "enhancement".

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the `LICENSE` file for details.

---

## 📧 Contact

Abdulbosit - Let's connect!

* **LinkedIn:** [Abdurahimov Abdulbosit](https://www.linkedin.com/in/abdulbosit-abdurahimov-a40b38356/)
* **Telegram:** [@Abdul_bosit_dev](https://t.me/Abdul_bosit_dev)
* **Email:** abdulbositabdurahimov260@gmail.com

Project Link: [https://github.com/AbdulbositAbdurahimovDeveloper/KUNDALIK.git](https://github.com/AbdulbositAbdurahimovDeveloper/KUNDALIK.git)
