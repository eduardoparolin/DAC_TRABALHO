# angular-app/Dockerfile

# 1) Build Angular
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# Adjust the config if you use a different output path / name
RUN npm run build -- --configuration=production

# 2) Serve with Nginx
FROM nginx:stable-alpine
# Copy build artifacts
COPY --from=build /app/dist/ /usr/share/nginx/html/
# Basic Nginx config for SPA routing (optional but recommended)
RUN apk add --no-cache bash
RUN printf 'server {\n  listen 80;\n  server_name _;\n  root /usr/share/nginx/html;\n  index index.html;\n  location / {\n    try_files $uri $uri/ /index.html;\n  }\n}\n' > /etc/nginx/conf.d/default.conf
EXPOSE 80
