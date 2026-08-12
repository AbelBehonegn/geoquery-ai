\# GeoQueryAI



GeoQueryAI is a full-stack GIS web application for managing land parcels on an interactive map.



I built this project to practice full-stack development and learn how React, Spring Boot, PostgreSQL, and PostGIS can work together with geographic data.



\## Features



\- View parcels on an interactive map

\- Draw new parcel polygons

\- Save parcels to a PostgreSQL/PostGIS database

\- Edit parcel information

\- Edit polygon boundaries

\- Delete parcels

\- Search for nearby parcels

\- Click inside a polygon to identify a parcel

\- Automatically calculate parcel area

\- Display parcel data using GeoJSON



\## Technologies



\### Frontend



\- React

\- Vite

\- JavaScript

\- React Leaflet

\- Leaflet

\- Leaflet Draw

\- OpenStreetMap



\### Backend



\- Java 17

\- Spring Boot

\- Spring Data JPA

\- Hibernate Spatial

\- JTS

\- Maven



\### Database



\- PostgreSQL

\- PostGIS



\## Project Structure



```text

GeoQueryAI/

│

├── backend/

│   ├── controller/

│   ├── dto/

│   ├── entity/

│   ├── exception/

│   ├── repository/

│   └── service/

│

├── frontend/

│   └── src/

│       ├── components/

│       ├── App.jsx

│       └── main.jsx

│

└── README.md

```



\## How It Works



The frontend sends requests to the Spring Boot REST API.



Spring Boot communicates with PostgreSQL/PostGIS to store parcel information and perform spatial queries.



```text

React + Leaflet

&#x20;      |

&#x20;      | REST API

&#x20;      v

Spring Boot

&#x20;      |

&#x20;      v

PostgreSQL + PostGIS

```



\## GIS Functions



The application currently supports:



\- Polygon storage

\- GeoJSON

\- Nearby parcel search

\- Point-in-polygon search

\- Polygon editing

\- Distance queries

\- Automatic parcel area calculation



\## Run the Backend



Make sure PostgreSQL/PostGIS is running.



Open PowerShell:



```powershell

cd C:\\GeoQueryAI\\backend

mvn spring-boot:run

```



The backend runs on:



```text

http://localhost:8080

```



\## Run the Frontend



Open another PowerShell window:



```powershell

cd C:\\GeoQueryAI\\frontend

npm install

npm run dev

```



Open:



```text

http://localhost:5173

```



\## Production Build



Backend:



```powershell

cd C:\\GeoQueryAI\\backend

mvn clean package

```



Frontend:



```powershell

cd C:\\GeoQueryAI\\frontend

npm run build

```



\## Current Status



The main GIS MVP is complete.



The application can create, read, update, and delete parcels. It also supports polygon drawing and editing, nearby searches, parcel identification, and automatic area calculation.



\## Future Improvements



I plan to add:



\- User login and authentication

\- User roles and permissions

\- Spring Security

\- Cloud deployment

\- HTTPS

\- Database backups

\- Monitoring

\- CI/CD with GitHub Actions

\- More automated tests



\## What I Learned



Through this project, I practiced:



\- Building REST APIs with Spring Boot

\- Building a React frontend

\- Connecting React to a Java backend

\- Working with PostgreSQL and PostGIS

\- Working with spatial data and GeoJSON

\- Creating interactive maps with Leaflet

\- Using Git and GitHub

\- Building frontend and backend applications for production



\## Project Status



\*\*MVP Complete\*\*

