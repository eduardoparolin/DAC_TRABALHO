# node-api/Dockerfile
FROM node:20-alpine
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
EXPOSE 3000
# Add curl for healthcheck
RUN apk add --no-cache curl
CMD ["node", "server.js"]
