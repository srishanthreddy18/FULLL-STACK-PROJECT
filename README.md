# Full Stack Student Portfolio

This project contains a Spring Boot backend and a React + Vite frontend.
The deployment setup below builds the frontend into static assets and serves it from the Spring Boot backend.

## Local Deployment with Docker Compose

1. Install Docker.
2. Run:
   ```bash
docker compose up --build
```
3. Access the app at `http://localhost:8080`.

## Environment Variables

The backend can be configured using environment variables:

- `DB_URL` - JDBC connection string
- `DB_USERNAME` - database username
- `DB_PASSWORD` - database password
- `JWT_SECRET` - JWT secret key
- `MAIL_USERNAME` - SMTP username
- `MAIL_PASSWORD` - SMTP password
- `APP_FRONTEND_ORIGIN` - allowed frontend origin for CORS in development

## Cloud Deployment

The app is container-ready with `Dockerfile`.
Any Docker-compatible host can deploy it as a single full-stack service.

For cloud deployment, point the host to the `Dockerfile` and configure a MySQL database service, including the environment variables above.

## Frontend API Configuration

The frontend uses `VITE_API_URL` when deployed separately. If the frontend and backend are served from the same origin, no additional configuration is required.
