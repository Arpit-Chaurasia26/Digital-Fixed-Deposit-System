# 🚀 Quick Start Guide - Digital Fixed Deposit System

## Prerequisites
- Node.js 18+ installed
- Backend server running on port 8080
- Terminal access

## 🏃 Run the Application (3 Commands)

### Step 1: Install Dependencies
```bash
cd "/Users/apritc/Desktop/Project/Digital-Fixed-Deposit-System-Frontend"
npm install
```

### Step 2: Start Development Server
```bash
npm run dev
```

### Step 3: Open Browser
Navigate to: **http://localhost:3000**

---

## 🧪 Run Tests

```bash
# Run all tests with UI
npm run test

# Run tests in headless mode
npm run test:run

# Generate coverage report
npm run coverage
```

---

## 📱 Application Features

### Public Access
- **Home Page** (/) - FD schemes, features, FAQs
- **Login** (/login) - User authentication
- **Register** (/register) - New user registration

### User Features (After Login)
- **Dashboard** (/user/dashboard) - Portfolio overview, maturing FDs
- **Book FD** (/user/book-fd) - Create new fixed deposit
- **My FDs** (/user/fd-list) - View all your FDs
- **FD Details** (/user/fd/:id) - Detailed FD information
- **Interest Timeline** (/user/fd/:id/interest) - View interest accruals
- **Break FD** (/user/fd/:id/break) - Premature withdrawal
- **Portfolio** (/user/portfolio) - Investment summary
- **Support Tickets** (/user/support) - View/create tickets

### Admin Features
- **Admin Dashboard** (/admin/dashboard) - System overview
- **FD Management** (/admin/fd-management) - Manage all FDs
- **Support Management** (/admin/support) - Handle tickets

---

## 🎨 Design System

### Colors (Zeta Theme)
- **Primary**: #1a237e (Deep Blue)
- **Secondary**: #00bcd4 (Cyan)
- **Accent**: #ff4081 (Pink)
- **Success**: #4caf50 (Green)
- **Warning**: #ff9800 (Orange)
- **Danger**: #f44336 (Red)

### Features
- Responsive design (mobile, tablet, desktop)
- Smooth animations throughout
- Modern card-based layouts
- Professional form styling

---

## 🔐 Test Credentials

### Creating Test User
1. Go to /register
2. Fill in details:
   - Name: John Doe
   - Email: john@example.com
   - Phone: 9876543210
   - Password: SecurePass@123
3. Click Register
4. Login with credentials

### Test FD Booking
1. Navigate to /user/book-fd
2. Select scheme (Standard 6 Months / Standard 12 Months / Senior Citizen 12 Months / Tax Saver 5 Years)
3. Enter principal (min ₹5,000)
4. Tenure is auto-set based on scheme
5. Click "Book FD"

---

## 🏗️ Project Structure

```
src/
├── assets/styles/     # Global SCSS (Zeta theme)
├── components/        # Navbar, Footer
├── router/            # Routes with guards
├── services/          # API services (5 modules)
├── store/             # Vuex modules (auth, fd, support)
├── types/             # TypeScript interfaces
├── utils/             # Helpers & validators
└── views/             # Page components (18 views)
    ├── auth/          # Login, Register
    ├── user/          # User dashboard & features
    └── admin/         # Admin management
```

---

## 📡 Backend Integration

### API Base URL
- **Development**: http://localhost:8080
- **Production**: Configure in `.env.production`

### Required Backend Endpoints
All endpoints from your Spring Boot backend are integrated:
- Authentication: /auth/*
- FD Management: /user/fd/*
- Withdrawals: /user/withdrawal/*
- Support: /user/support/*
- Admin: /admin/*

---

## 🔧 Available Scripts

| Command | Description |
|---------|-------------|
| `npm install` | Install all dependencies |
| `npm run dev` | Start development server |
| `npm run build` | Build for production |
| `npm run preview` | Preview production build |
| `npm run test` | Run tests with UI |
| `npm run test:run` | Run tests (headless) |
| `npm run coverage` | Generate test coverage |

---

## 📦 Key Dependencies

- **Vue 3.5.25** - Progressive framework
- **TypeScript 5.9.3** - Type safety
- **Vuex 4.1.0** - State management
- **Vue Router 4.6.4** - Routing
- **Axios 1.13.5** - HTTP client
- **Vite 8** - Build tool
- **Vitest 4.0.18** - Testing framework

---

## 🎯 Quick Testing Flow

### 1. Register & Login (2 min)
- Register new user
- Login with credentials
- Verify dashboard loads

### 2. Book FD (2 min)
- Go to Book FD
- Select Standard 12 Months scheme
- Principal: ₹10,000
- Tenure auto-filled (12 months)
- Submit & verify

### 3. View Portfolio (1 min)
- Check dashboard stats
- View FD list
- Click FD details

### 4. Interest Timeline (1 min)
- From FD details, click "View Interest"
- Verify timeline shows accruals

### 5. Break FD (2 min)
- Click "Break FD"
- Review penalty preview
- Confirm withdrawal

### 6. Support Tickets (2 min)
- Create new ticket
- Add subject & description
- View in ticket list

**Total Testing Time**: ~10 minutes for full flow

---

## 🐛 Troubleshooting

### Port Already in Use
```bash
# Kill process on port 3000
lsof -ti:3000 | xargs kill -9
# Or use different port
npm run dev -- --port 3001
```

### Backend Not Running
```bash
# Start backend first
cd Digital-Fixed-Deposit-System-Backend
mvn spring-boot:run
```

### Dependencies Not Installing
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
```

### TypeScript Errors
```bash
# Build will resolve them
npm run build
```

---

## 📚 Documentation

- **PROJECT_README.md** - Complete setup & features
- **IMPLEMENTATION_SUMMARY.md** - Technical details
- **FINAL_CHECKLIST.md** - Verification checklist

---

## ✨ Features Highlights

✅ **5 Complete Modules**: Auth, FD Management, Interest, Withdrawal, Support  
✅ **18 View Components**: All pages implemented  
✅ **100+ Unit Tests**: Comprehensive coverage  
✅ **Responsive Design**: Works on all devices  
✅ **Zeta Theme**: Professional styling  
✅ **Real Backend Integration**: No hardcoded data  
✅ **Role-Based Access**: User & Admin dashboards  
✅ **Form Validations**: Real-time feedback  
✅ **Smooth Animations**: Enhanced UX  
✅ **Production Ready**: Optimized build  

---

## 🎉 You're All Set!

Your Digital Fixed Deposit System frontend is **ready to run**!

**Start exploring**: `npm run dev` → http://localhost:3000

---

**Need Help?**
- Check PROJECT_README.md for detailed documentation
- Review IMPLEMENTATION_SUMMARY.md for technical architecture
- All 5 modules are fully functional and tested

**Happy Coding! 🚀**
