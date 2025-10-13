# node-api/Dockerfile
FROM node:22-alpine
WORKDIR /usr/src/app
COPY package*.json ./
# Install all dependencies (including devDependencies for build)
RUN npm ci
# Copy source files
COPY . .
# Build TypeScript
RUN npm run build
# Remove devDependencies for production
RUN npm prune --production
EXPOSE 3000
# Add curl for healthcheck
RUN apk add --no-cache curl
# Run the built JavaScript (entry point)
CMD ["node", "dist/index.js"]
