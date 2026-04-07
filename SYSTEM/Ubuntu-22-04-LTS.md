# CẤU HÌNH VPS Ubuntu 22.04-LTS

## 0) Chuẩn bị trước khi cài

- VPS mới cài Ubuntu 22.04-LTS (fresh install)
- **IP tĩnh** của VPS 
- **Domain** trỏ A record về IP VPS (nếu muốn SSL)
- Mở port ở nhà cung cấp VPS (nếu họ có firewall riêng)

| Nhóm | Cổng                             | Ghi chú |
|------|----------------------------------|---------|
| SSH | 24700 (hoặc custom)              | Quản trị server |
| Web | 80, 443                          | Website + SSL |
| Mail | 25, 465, 587, 110, 995, 143, 993 | Nếu dùng mail server |
| DB | 3306                             | Chỉ mở ra ngoài khi thật cần |

## 1) Cập nhật hệ thống + cài tool cơ bản

```bash
apt update && apt upgrade -y
apt install -y curl wget unzip zip nano vim git ufw build-essential
```

### 1.1) Cấu hình Git (cần thiết cho deployment)
```bash
# Cấu hình Git global (cho root user)
git config --global user.name "conghiale"
git config --global user.email "legend.mighty28102002@gmail.com"
git config --global init.defaultBranch main

# Tạo SSH key cho GitHub/GitLab (optional)
ssh-keygen -t ed25519 -C "legend.mighty28102002@gmail.com" -f ~/.ssh/id_ed25519 -N ""

# Hiển thị public key để add vào GitHub/GitLab
echo "=== SSH PUBLIC KEY ==="
cat ~/.ssh/id_ed25519.pub
echo "=== Copy key này và thêm vào GitHub/GitLab Settings > SSH Keys ==="
```

Thiết lập hostname:
```bash
hostnamectl set-hostname maison-art.yourdomain.com
hostname -f
```

## 2) Tạo user quản trị (khuyến nghị )

```bash
adduser deploy # not use
usermod -aG sudo deploy # - not use - cho deploy quyền sudo nếu cần

# Cấu hình Git cho user deploy
su - deploy << 'EOF'
git config --global user.name "Deploy User"
git config --global user.email "deploy@yourdomain.com"
git config --global init.defaultBranch main

# Tạo SSH key cho user deploy
ssh-keygen -t ed25519 -C "deploy@yourdomain.com" -f ~/.ssh/id_ed25519 -N ""

# Hiển thị public key
echo "=== SSH PUBLIC KEY FOR DEPLOY USER ==="
cat ~/.ssh/id_ed25519.pub
echo "=== Copy key này và thêm vào repository SSH Keys ==="

# Test SSH connection (sẽ fail lần đầu, chỉ để add host key)
ssh -T git@github.com 2>/dev/null || echo "SSH key setup completed"
EOF
```

Bảo mật SSH - mở file:
```bash
nano /etc/ssh/sshd_config
```

Sửa:
- `PermitRootLogin no`  
- (tuỳ chọn) `Port 22` → đổi cổng khác

Restart SSH:
```bash
systemctl restart ssh
```

## 3) Firewall (UFW) - mở các cổng cần thiết

```bash
# Mở cổng cần thiết
ufw allow 24700/tcp  # SSH (đổi port nếu cần)
ufw allow http
ufw allow https

# Kích hoạt UFW
ufw enable

# Xem trạng thái
ufw status
```

> **Lưu ý**: Nếu đổi cổng SSH, nhớ mở cổng mới trước khi logout: `ufw allow <new_port>`

## 4) Cài Java 17 (OpenJDK)

```bash
# ================================
# 1. Cài đặt Java 17 (OpenJDK)
# ================================
sudo apt update
sudo apt install -y openjdk-17-jdk

# ================================
# 2. Kiểm tra version
# ================================
java -version
javac -version

# ================================
# 3. Xác định đường dẫn JAVA_HOME
# ================================
readlink -f $(which java)
# Ví dụ output:
# /usr/lib/jvm/java-17-openjdk-amd64/bin/java

# JAVA_HOME sẽ là:
# /usr/lib/jvm/java-17-openjdk-amd64

# ================================
# 4. Tạo symlink dễ nhớ (java-17)
# ================================
sudo ln -sfn /usr/lib/jvm/java-17-openjdk-amd64 /usr/lib/jvm/java-17

# ================================
# 5. Set JAVA_HOME + PATH (system-wide)
# ================================
nano /etc/profile.d/java.sh
# Thêm vào file java.sh:
export JAVA_HOME=/usr/lib/jvm/java-17
export PATH=$JAVA_HOME/bin:$PATH

# Load lại biến môi trường
source /etc/profile

# ================================
# 6. Kiểm tra lại
# ================================
echo $JAVA_HOME
which java
java -version
```

## 5) Cài WildFly

### 5.1) Tạo user chạy WildFly
```bash
# Tạo user system để chạy WildFly (production sử dung deploy cho wildfly và nodejs), không có shell login
useradd --system --home-dir /opt/wildfly --shell /sbin/nologin wildfly
```

### 5.2) Download và cài WildFly 27+
```bash
cd /opt
wget https://github.com/wildfly/wildfly/releases/download/27.0.1.Final/wildfly-27.0.1.Final.tar.gz
tar -xzf wildfly-27.0.1.Final.tar.gz
ln -s wildfly-27.0.1.Final wildfly
chown -R wildfly:wildfly /opt/wildfly-27.0.1.Final /opt/wildfly
```

### 5.3) Tạo instance maison-art
```bash
cp -a /opt/wildfly/standalone /opt/wildfly/maison-art
chown -R wildfly:wildfly /opt/wildfly/maison-art
```

### 5.4) Tạo systemd service template
```bash
cat > /etc/systemd/system/wildfly@.service << 'EOF'
[Unit]
Description=WildFly Instance %i
After=network.target

[Service]
Type=simple
User=wildfly
Group=wildfly
WorkingDirectory=/opt/wildfly

# Environment file cho từng instance
EnvironmentFile=-/etc/default/wildfly-%i

# Java environment
Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
Environment=PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin

ExecStart=/opt/wildfly/bin/standalone.sh \
        -b 127.0.0.1 \
        -bmanagement 127.0.0.1 \
        --server-config=standalone.xml \
        -Djboss.server.base.dir=/opt/wildfly/%i \
        -Djboss.socket.binding.port-offset=${PORT_OFFSET}

Restart=on-failure
RestartSec=5
LimitNOFILE=65535

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
```

### 5.5) Tạo file environment cho instance maison-art
```bash
cat > /etc/default/wildfly-maison-art << 'EOF'
PORT_OFFSET=100
EOF
```

### 5.6) Enable và start service
```bash
systemctl enable wildfly@maison-art
systemctl start wildfly@maison-art
systemctl status wildfly@maison-art
```

### 5.7) Kiểm tra
```bash
# Kiểm tra log
journalctl -u wildfly@maison-art -f

# Test local
curl -I http://127.0.0.1:8180/
```

## 6) Deploy WAR file

```bash
# Copy WAR file vào deployments
cp /path/to/your-app.war /opt/wildfly/maison-art/deployments/artanddecor.war

# Set quyền
chown wildfly:wildfly /opt/wildfly/maison-art/
chown wildfly:wildfly /home/maison-art/storage/

# Restart service
systemctl restart wildfly@maison-art
```

## 7) Cài MySQL

```bash
# Cài MySQL Server
apt install -y mysql-server

# Bảo mật MySQL
mysql_secure_installation
```

### 7.1) Tạo database cho maison-art
```bash
mysql -u root -p
```

```sql
-- Tạo database
DROP DATABASE IF EXISTS `ART_AND_DECOR`;
     
CREATE DATABASE IF NOT EXISTS `ART_AND_DECOR`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Tạo user
CREATE USER 'maison-art'@'localhost' IDENTIFIED BY 'db@maison-art';

-- Cấp quyền
GRANT ALL PRIVILEGES ON `ART_AND_DECOR`.* TO 'maison-art'@'localhost';
FLUSH PRIVILEGES;

-- Kiểm tra
SHOW DATABASES;
SELECT user,host FROM mysql.user WHERE user='maison-art';

-- Chạy file SQL
-- chạy từ terminal
mysql -u maison-art -p ART_AND_DECOR < RUN_ALL_DATABASE_SCRIPTS.sql;
db@maison-art

-- chạy từ bên trong MySQL CLI
SOURCE /full/path/to/file.sql;
```

## 8) SSL với Certbot

```bash
# Cài Certbot
apt install -y certbot python3-certbot-nginx

# Tạo SSL certificate
certbot --nginx -d maisonart.vn -d www.maisonart.vn

# Auto-renewal
systemctl enable certbot.timer

```

## 9) Cài Nginx (Reverse Proxy)

```bash
apt install -y nginx

# Enable và start
systemctl enable nginx
systemctl start nginx
```

### 9.1) Cấu hình reverse proxy cho maison-art
```bash
nano /etc/nginx/sites-available/maison-art.conf
```

```nginx
# HTTP → HTTPS redirect
server {
    listen 80;
    server_name maisonart.vn www.maisonart.vn;

    return 301 https://$host$request_uri;
}

# HTTPS server
server {
    listen 443 ssl http2;
    server_name maisonart.vn www.maisonart.vn;

    # SSL (Certbot sẽ thêm)
    ssl_certificate /etc/letsencrypt/live/maisonart.vn/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/maisonart.vn/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    client_max_body_size 50M;

    # ======================
    # NEXTJS FRONTEND
    # ======================
    location / {
        proxy_pass http://127.0.0.1:3000;

        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # ======================
    # JAVA API
    # ======================
    location /api/ {
        proxy_pass http://127.0.0.1:8180;

        proxy_http_version 1.1;

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    } 
}
```

```bash
# Enable site
ln -s /etc/nginx/sites-available/maison-art.conf /etc/nginx/sites-enabled/
systemctl reload nginx
```

## 10) Cấu hình WildFly proxy forwarding

Sửa file `/opt/wildfly/maison-art/configuration/standalone.xml`:

```xml
<http-listener name="default" socket-binding="http" proxy-address-forwarding="true"/>
```

Restart WildFly:
```bash
systemctl restart wildfly@maison-art
```

## 11) Cài Node.js 22 + Deploy Next.js Client

### 11.1) Cài Node.js 22
```bash
# Cài Node.js 22 từ NodeSource repository
curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
apt install -y nodejs

# Kiểm tra version
node --version
npm --version

# Cài Yarn (optional)
npm install -g yarn
```

### 11.2) Cài PM2 (Process Manager)
```bash
# Cài PM2 globally
npm install -g pm2

# Tạo PM2 startup script
pm2 startup
# Chạy lệnh được suggest bởi PM2 (thường là: sudo env PATH=...)
```

### 11.3) Deploy Next.js Client
```bash
# Tạo thư mục và set quyền cho deploy user
mkdir -p /var/www/maison-art-client
chown -R deploy:deploy /var/www/maison-art-client

# Chuyển sang user deploy để thực hiện deployment
su - deploy # not use
cd /var/www/maison-art-client

# Clone source code từ repository
# Thay YOUR_REPO_URL bằng repository thực tế
git clone git@github.com:annhducit/painting-store.git .
# Hoặc sử dụng HTTPS nếu chưa setup SSH:
# git clone https://github.com/your-username/maison-art-client.git .

# Kiểm tra Node.js version compatibility
node --version
npm --version

# Cài dependencies (sử dụng npm ci cho production)
if [ -f "package-lock.json" ]; then
    npm ci
else
    npm install
fi

# Build production (tạo folder .next, build output)
npm run build

# Kiểm tra build thành công
if [ -d ".next" ] && [ -f ".next/BUILD_ID" ]; then
    echo "✅ Build successful!"
    ls -la .next/
else
    echo "❌ Build failed!"
    exit 1
fi
```

### 11.4) Cấu hình PM2 cho Next.js
```bash
# Vẫn trong user deploy context
# Kiểm tra package.json có script start không
if ! grep -q '"start"' package.json; then
    echo "❌ Missing start script in package.json"
    echo "Add this to package.json scripts: \"start\": \"next start\""
    exit 1
fi

# Tạo thư mục logs
mkdir -p logs

# Tạo file ecosystem.config.js với cấu hình tối ưu cho Next.js production
cat > ecosystem.config.js << 'EOF'
module.exports = {
  apps: [{
    name: 'maison-art-client',
    script: 'node_modules/.bin/next',
    args: 'start',
    cwd: '/var/www/maison-art-client',
    instances: 1, // có thể scale lên nhiều instance nếu cần 2/3 cho production
    exec_mode: 'fork', // 'cluster' mode có thể dùng nhưng cần cẩn thận với stateful features
    autorestart: true,
    watch: false,
    max_memory_restart: '1G',
    env: {
      NODE_ENV: 'production',
      PORT: 3000,
      HOSTNAME: '0.0.0.0'
    },
    error_file: 'logs/err.log',
    out_file: 'logs/out.log',
    log_file: 'logs/combined.log',
    time: true,
    log_date_format: 'YYYY-MM-DD HH:mm:ss Z'
  }]
};
EOF

# Kiểm tra PM2 đã được cài đặt cho user deploy
which pm2 || {
    echo "❌ PM2 not found for user deploy"
    echo "Run: sudo npm install -g pm2"
    exit 1
}

# Start app với PM2
pm2 start ecosystem.config.js

# Save PM2 config để auto-start sau reboot
pm2 save

# Kiểm tra status
pm2 status

# Test local connection
sleep 3
curl -I http://localhost:3000 || echo "⚠️ App might still be starting..."

# Xem logs để debugging
pm2 logs maison-art-client --lines 10
```

### 11.5) Cấu hình Nginx cho Next.js + API
Cập nhật file Nginx config để serve cả client và API:

```bash
nano /etc/nginx/sites-available/maison-art.conf
```

```nginx
server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    
    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;
    
    # SSL certificates (Certbot sẽ thêm sau)
    
    # API routes - proxy to WildFly
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Server $host;
    }
    
    # Next.js client - proxy to Node.js
    location / {
        proxy_pass http://127.0.0.1:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        
        # WebSocket support for Next.js dev
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_cache_bypass $http_upgrade;
    }
    
    # Static files caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        proxy_pass http://127.0.0.1:3000;
        proxy_cache_valid 200 1d;
        proxy_set_header Host $host;
        add_header Cache-Control "public, max-age=86400";
    }
}
```

```bash
# Test và reload Nginx
nginx -t
systemctl reload nginx
```

## 12) Các lệnh quản lý thường dùng

### Next.js Client (PM2)
```bash
# Quản lý PM2
pm2 start maison-art-client
pm2 stop maison-art-client
pm2 restart maison-art-client
pm2 reload maison-art-client  # Zero-downtime restart

# Xem status và logs
pm2 status
pm2 logs maison-art-client
pm2 logs maison-art-client --lines 100

# Monitor real-time
pm2 monit

# Deploy code mới (workflow chuẩn)
cd /var/www/maison-art-client

# Backup current version (tùy chọn)
# cp -r .next .next.backup.$(date +%Y%m%d_%H%M%S)

# Pull latest code
git pull origin staging

# Kiểm tra có thay đổi dependencies không
if git diff HEAD~1 HEAD --name-only | grep -q package.json; then
    echo "📦 Dependencies changed, reinstalling..."
    if [ -f "package-lock.json" ]; then
        npm ci
    else
        npm install
    fi
fi

# Build lại
npm run build

# Kiểm tra build thành công
if [ -d ".next" ] && [ -f ".next/BUILD_ID" ]; then
    echo "✅ Build successful! Restarting PM2..."
    pm2 reload maison-art-client --wait-ready
    echo "🚀 Deployment completed!"
else
    echo "❌ Build failed! Rolling back..."
    # git reset --hard HEAD~1  # uncomment nếu muốn auto rollback
    exit 1
fi

# PM2 management
pm2 save  # Save current processes
pm2 resurrect  # Restore saved processes
pm2 startup  # Generate startup script
```

### WildFly
```bash
# Quản lý service
systemctl start wildfly@maison-art
systemctl stop wildfly@maison-art  
systemctl restart wildfly@maison-art
systemctl status wildfly@maison-art

# Xem log
journalctl -u wildfly@maison-art -f
tail -f /opt/wildfly/maison-art/log/server.log

# Deploy WAR mới
cp new-app.war /opt/wildfly/maison-art/deployments/ROOT.war
chown wildfly:wildfly /opt/wildfly/maison-art/deployments/ROOT.war
systemctl restart wildfly@maison-art
```

### Nginx
```bash
# Test config
nginx -t

# Reload
systemctl reload nginx

# Xem log
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### MySQL
```bash
# Login
mysql -u root -p

# Login với user app  
mysql -u maison-art -p ART_AND_DECOR

# Backup database
mysqldump -u maison-art -p ART_AND_DECOR > backup.sql

# Restore database
mysql -u maison-art -p ART_AND_DECOR < backup.sql
```

### System Monitoring
```bash
# Xem port đang lắng nghe
ss -tulnp

# Kiểm tra dung lượng
df -h

# Xem RAM/CPU
htop
free -h

# Xem process Java
ps aux | grep java
```

## 13) Firewall cho production

```bash
# Chỉ mở port cần thiết
ufw --force reset
ufw default deny incoming
ufw default allow outgoing

# SSH (đổi port nếu cần)
ufw allow 22

# Web
ufw allow 80
ufw allow 443

# Enable
ufw enable

# Kiểm tra
ufw status numbered
```

## Checklist triển khai

### Backend (API Server)
- [ ] Update server + cài tool cơ bản
- [ ] Thiết lập hostname + user quản trị
- [ ] Cấu hình firewall UFW
- [ ] Cài Java 17
- [ ] Cài WildFly + tạo instance maison-art
- [ ] Tạo systemd service wildfly@maison-art
- [ ] Deploy WAR file
- [ ] Cài MySQL + tạo database ART_AND_DECOR

### Frontend (Client Web)
- [ ] Cài Node.js 22
- [ ] Cài PM2 process manager
- [ ] Cấu hình Git và SSH keys
- [ ] Clone Next.js client code
- [ ] Install dependencies và build
- [ ] Cấu hình PM2 ecosystem cho Next.js
- [ ] Test Next.js app chạy local (port 3000)

### Infrastructure
- [ ] Cài Nginx reverse proxy
- [ ] Cấu hình Nginx cho Next.js + API routing
- [ ] Cấu hình SSL với Certbot
- [ ] Test hoạt động end-to-end:
  - [ ] Next.js accessible via https://domain.com
  - [ ] API accessible via https://domain.com/api/
  - [ ] Database connection từ API
  - [ ] Static files (JS, CSS, images) load correctly
  - [ ] WebSocket connection (nếu có) hoạt động
  
### Troubleshooting Common Issues
- **Next.js 500 error**: Check PM2 logs với `pm2 logs maison-art-client`
- **API không accessible**: Kiểm tra WildFly status và Nginx proxy config
- **Build fails**: Kiểm tra Node.js version compatibility và dependencies
- **Permission denied**: Đảm bảo user deploy có quyền trên `/var/www/maison-art-client`

> **Lưu ý quan trọng**: 
> - **Backend**: WildFly chỉ bind 127.0.0.1 (API endpoint: `/api/`)
> - **Frontend**: Next.js chạy port 3000 nội bộ (root path: `/`)
> - **Proxy**: Nginx reverse proxy xử lý SSL và route traffic
>   - `/api/*` → WildFly (127.0.0.1:8080)
>   - `/*` → Next.js (127.0.0.1:3000)
> - **Database**: MySQL chỉ cho phép connect từ localhost
> - **Tên dự án**: **maison-art**
> - **Services**: **wildfly@maison-art**, **maison-art-client** (PM2)
> - **Database**: **ART_AND_DECOR**

### Kiến trúc hệ thống:
```
Internet → Nginx (443/80) → {
  /api/* → WildFly (8080) → MySQL (3306)
  /*     → Next.js (3000)
}
```