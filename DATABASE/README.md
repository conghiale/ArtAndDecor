# ART AND DECOR DATABASE SETUP

## Overview
This folder contains all database scripts for the Art and Decor e-commerce project.

## Files Description

| File | Description | Execution Order |
|------|-------------|-----------------|
| `RUN_ALL_DATABASE_SCRIPTS.sql` | **Master script** - Executes all other scripts in correct order | 1 (Main) |
| `CREATE_DB_ART_AND_DECOR.sql` | Creates database schema, tables, indexes, and initial reference data | 2 (Auto) |
| `INSERT_SAMPLE_DATA.sql` | Inserts sample data for testing and development | 3 (Auto) |
| `CLEAR_SAMPLE_DATA.sql` | Clears sample data (optional, manual execution) | 4 (Manual) |
| `run_database_setup.bat` | Windows batch script for easy execution | - (Helper) |

## Quick Setup

### Option 1: Using Navicat/GUI Tools
```sql
-- Open Navicat, connect to MySQL server, then run:
-- File: SETUP_FOR_NAVICAT.sql (basic setup)
-- Then manually run: CREATE_DB_ART_AND_DECOR.sql
-- Then manually run: INSERT_SAMPLE_DATA.sql
```

### Option 2: Using Batch Script (Windows)
```bash
# Navigate to DATABASE folder
cd DATABASE

# Double-click or run in command prompt
run_database_setup.bat
```

### Option 3: Using MySQL Command Line
```bash
# Mở Command Prompt
cd "D:\Data\PROJECT_PERSONAL\WEBSITE_ART_DECOR_HUB\DATABASE"

# Chạy với XAMPP MySQL (không password - mặc định)
"D:\App\Xampp\mysql\bin\mysql.exe" -u root < RUN_ALL_DATABASE_SCRIPTS.sql

# Chạy với XAMPP MySQL (có password)
"D:\App\Xampp\mysql\bin\mysql.exe" -u root -p < RUN_ALL_DATABASE_SCRIPTS.sql
```

## What Gets Created

### Database Structure
- **Database**: `ART_AND_DECOR` with UTF8MB4 charset
- **Tables**: 31 tables including:
  - User management (USER, USER_ROLE, USER_PROVIDER)
  - Product catalog (PRODUCT, PRODUCT_CATEGORY, PRODUCT_TYPE, etc.)
  - Order system (ORDERS, ORDER_STATE, CART, CART_ITEM, etc.)
  - Content management (BLOG, CONTACT, POLICY)

### Sample Data
- 8 sample users (admin, manager, staff, customers)
- 12 sample products (paintings, drawing tools, decorations)
- 15 product reviews with likes
- Sample orders, carts, and payment records
- Blog posts and policies

## Database Configuration

Update your `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ART_AND_DECOR?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=your_password
```

## Managing Sample Data

### To clear sample data only:
```sql
SOURCE CLEAR_SAMPLE_DATA.sql;
```

### To re-insert sample data:
```sql
SOURCE INSERT_SAMPLE_DATA.sql;
```

## Prerequisites

1. **MySQL Server** 8.0+ running
2. **User privileges** to CREATE/DROP databases
3. **Network access** to MySQL server (localhost:3306)

## Troubleshooting

### Common Issues:

1. **"SOURCE command not recognized" (Navicat/GUI Tools)**
   - SOURCE command only works in MySQL command line
   - Use `SETUP_FOR_NAVICAT.sql` instead
   - Or manually execute each SQL file separately

2. **"MariaDB server version" error message**
   - Check actual database type: `SELECT VERSION();`
   - MariaDB is MySQL-compatible but has syntax differences
   - Ensure you're connecting to MySQL, not MariaDB

3. **"MySQL command not found"**
   - Add MySQL bin directory to PATH
   - Example: `C:\Program Files\MySQL\MySQL Server 8.0\bin`

4. **"Access denied"**
   - Check username/password
   - Ensure user has sufficient privileges

5. **"Database already exists"**
   - The script will skip creation and continue
   - Or manually drop: `DROP DATABASE ART_AND_DECOR;`

6. **Foreign key constraint errors**
   - Ensure you're running scripts in correct order
   - Use master script `RUN_ALL_DATABASE_SCRIPTS.sql`

## Support

For issues or questions:
1. Check the execution logs for specific error messages
2. Verify MySQL server status and credentials
3. Ensure all SQL files are in the same directory

---

**Note**: The master script includes verification queries to confirm successful setup.