
# Facebook App

Projekt polegał na stworzeniu aplikacji webowej w Java Spring Boot z wykorzystaniem Thymeleaf, która   umożliwia użytkownikom przesyłanie i analizowanie danych z konwersacji Facebooka w formacie JSON. Aplikacja pozwala na wgrywanie plików eksportowanych z Messenger Facebooka, które następnie są walidowane pod kątem formatu i struktury. Po przesłaniu pliku użytkownik ma dostęp do panelu z listą dostępnych konwersacji, zawierających podstawowe informacje takie jak nazwa pliku, liczba uczestników. Z widoku tego panelu możliwe jest też pobranie danych w PDF, wyświetlenie danych na stronie razem z wykresami oraz usunięcie pliku. Po kliknięciu na przycisk dane użytkownik ma możliwość wybrania typu danych który chce wyświetlić.


## Prerequisites
- Java 17
- Maven

## Installation

Install my-project with npm

```bash
https://github.com/Immunelele/FacebookApp.git
cd FacebookApp
mvn clean install
```
    
## Download Path
Before starting you have to change the " dw.path = C:\\upload\\ " in application.properties, to your own desired folder on the computer. So the files can be imported and then analyzed etc.
## Features

- File import
The user is able to import multiple .json files using the dropbox on the "Wgrywanie" tab of the application.

- File preview
After clicking button "Pliki" the site will show the available files that were previously imported by the user. From this tab you can perform all actions regarding the files, see the name of the files as well as number of participants in each one of them.
- Data selection
Upon clicking on the "Dane" button user is shown on the new tab overall data like how many messages were sent in this conversation but more importantly he can choose the desired data that he wants to see. After that he will be shown table with data as well as a chart visualizing it. 
- Data export to PDF
There is a possibility to export all the data regarding desired file pressing one button that is "Eksport". All the data will be packed into a PDF file and downloaded on your computer.
- File delete
At last there is an option to delete files directly from the site using button with a small thrash bin icon.

