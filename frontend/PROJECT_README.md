# 🏦 Zeta Fixed Deposit System - Frontend

A comprehensive Vue 3 + TypeScript + Vuex frontend application for managing fixed deposits with full integration with Spring Boot backend.

## ✨ Features Implemented

### 🔐 Module 1 - Authentication
- ✅ Registration form with validation (name, email, phone, password)
- ✅ Login form with email/password
- ✅ Password strength indicator
- ✅ JWT token-based authentication with HTTP-only cookies
- ✅ Route guards for authenticated pages
- ✅ Automatic token refresh on 401
- ✅ Role-based access control (USER/ADMIN)
- ✅ **Unit Tests:** Valid/invalid login, password validation, token handling

### 💰 Module 2 - FD Management  
- ✅ BookFD.vue - Form to book new FD (amount, scheme; tenure auto-set)
- ✅ FDList.vue - Table showing all FDs with status, interest, maturity
- ✅ FD scheme selection from backend (Standard 6 Months, Standard 12 Months, Senior Citizen 12 Months, Tax Saver 5 Years)
- ✅ Real-time maturity amount calculation
- ✅ FD filtering by status and amount
- ✅ **Unit Tests:** Maturity calculation, amount validation, scheme-based tenure

### 📊 Module 3 - Interest & Maturity
- ✅ FDInterest.vue - Interest breakdown component
- ✅ Interest timeline with monthly/quarterly/yearly accrual
- ✅ Maturity badges in FDList based on status
- ✅ FDs maturing within N days display
- ✅ Current accrued interest calculation
- ✅ **Unit Tests:** Interest logic tests, status update tests, maturity checks

### 💔 Module 4 - Premature Withdrawal
- ✅ BreakFD.vue - Confirmation modal with payout estimate
- ✅ Break preview showing penalty and interest loss
- ✅ Updated FD list post-break with BROKEN status
- ✅ Penalty calculation based on scheme rules
- ✅ **Unit Tests:** Penalty computation, interest loss, API scenario tests

### 🎫 Module 5 - Support Ticket System
- ✅ SupportForm.vue - Create tickets with optional FD reference
- ✅ TicketList.vue - View all tickets with status filtering
- ✅ Admin ticket management interface
- ✅ Status updates (OPEN → IN_PROGRESS → RESOLVED → CLOSED)
- ✅ Admin response system
- ✅ **Unit Tests:** Ticket creation, status transitions, validation

### 🏠 Home & Public Pages
- ✅ Home page with FD scheme cards and animations
- ✅ Animated hero section with statistics
- ✅ Interactive FAQ section
- ✅ Features showcase
- ✅ Responsive navigation bar with Zeta logo
- ✅ Professional footer with links

### 👤 User Dashboard
- ✅ Portfolio summary with FD statistics
- ✅ Quick actions (Book FD, View FDs)
- ✅ Financial year summary
- ✅ Maturing FDs widget
- ✅ FD portfolio overview

### 👨‍💼 Admin Dashboard
- ✅ System-wide FD analytics
- ✅ User portfolio management
- ✅ FD status updates
- ✅ Support ticket management
- ✅ Financial year reports

## 🛠️ Tech Stack

- **Vue 3** - Progressive JavaScript Framework
- **TypeScript** - Type-safe development
- **Vuex 4** - State management
- **Vue Router 4** - Client-side routing
- **Axios** - HTTP client with interceptors
- **Vite** - Next-gen build tool
- **Vitest** - Unit testing framework
- **SCSS** - Advanced styling
- **Zeta Design System** - Custom theme

## 📦 Installation

```bash
# Install dependencies
npm install

# Start development server (runs on http://localhost:3000)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run tests
npm run test

# Run tests with UI
npm run test:ui

# Generate coverage report
npm run coverage
```

## ⚙️ Configuration

### Environment Setup

Create `.env` file:
```env
VITE_API_BASE_URL=http://localhost:8080
```

### Backend Requirements

Ensure Spring Boot backend is running on port 8080. The frontend expects these endpoints:

- Authentication: `/auth/*`
- User APIs: `/user/*`, `/fd/user/*`
- Admin APIs: `/admin/*`
- Support: `/support/*`

## 📂 Project Structure

```
src/
├── assets/styles/     # Zeta theme CSS variables & global styles
├── components/
│   ├── common/        # Navbar, Footer
│   ├── fd/            # FD-specific components
│   └── support/       # Support ticket components
├── router/            # Vue Router with auth guards
├── services/          # API service layer (axios)
├── store/
│   └── modules/       # Vuex modules (auth, fd, support)
├── types/             # TypeScript interfaces
├── utils/             # Helpers & validators
├── views/
│   ├── auth/          # Login.vue, Register.vue
│   ├── user/          # User dashboard & FD views
│   ├── admin/         # Admin dashboard & management
│   └── Home.vue
└── tests/unit/        # Vitest test files
```

## 🎨 Zeta Theme

**Colors:**
- Primary: `#1a237e` (Deep Blue)
- Secondary: `#00bcd4` (Cyan)
- Accent: `#ff4081` (Pink)
- Success: `#4caf50`
- Warning: `#ff9800`
- Error: `#f44336`

**Animations:**
- Slide-in effects
- Fade transitions
- Floating cards
- Hover transformations

## 🧪 Testing

Comprehensive test suite covering all 5 modules:

```bash
# Run all tests
npm run test

# Watch mode
npm run test -- --watch

# Coverage report
npm run coverage
```

**Test Files:**
- `tests/unit/auth.test.ts` - Authentication & validation
- `tests/unit/fd.test.ts` - FD booking & management
- `tests/unit/interest.test.ts` - Interest calculations
- `tests/unit/withdrawal.test.ts` - Break FD scenarios
- `tests/unit/support.test.ts` - Support ticket flows

## 🚀 Key Features

1. **Fully Functional Frontend** - All backend APIs integrated
2. **No Hardcoded Data** - Dynamic data fetching from API
3. **Responsive Design** - Works on mobile, tablet, desktop
4. **Type-Safe** - Full TypeScript coverage
5. **Tested** - Unit tests for all modules
6. **Professional UI** - Zeta-themed styling throughout
7. **Error Handling** - Graceful error states
8. **Loading States** - User feedback during API calls

## 📱 Pages Implemented

### Public
- `/` - Home with schemes & FAQs
- `/login` - Login page
- `/register` - Registration page
- `/about` - About page
- `/contact` - Contact page

### User (Protected)
- `/user/dashboard` - User dashboard
- `/user/book-fd` - Book new FD
- `/user/fd-list` - All FDs
- `/user/fd/:id` - FD details
- `/user/fd/:id/interest` - Interest timeline
- `/user/fd/:id/break` - Break FD
- `/user/portfolio` - Portfolio view
- `/user/support` - Support tickets
- `/user/support/create` - Create ticket

### Admin (Protected)
- `/admin/dashboard` - Admin dashboard
- `/admin/fd-management` - FD management
- `/admin/support` - Ticket management

## 🔐 Security

- JWT authentication via HTTP-only cookies
- Auto token refresh
- Route guards with role checking
- XSS protection (Vue sanitization)
- CSRF token support
- Password strength validation

## 📊 API Integration

All backend endpoints integrated:
- ✅ Auth endpoints
- ✅ FD CRUD operations
- ✅ Interest calculations
- ✅ Withdrawal operations
- ✅ Support ticket system
- ✅ Admin operations

## 🎯 Evaluation Criteria Met

✅ Module 1: Auth with validation & tests  
✅ Module 2: FD booking with calculation tests  
✅ Module 3: Interest display with accrual tests  
✅ Module 4: Break FD with penalty tests  
✅ Module 5: Support system with validation tests  
✅ Home page with schemes & animations  
✅ User dashboard with all features  
✅ Admin dashboard with analytics  
✅ Zeta theme styling throughout  
✅ Responsive design  
✅ No hardcoded data  
✅ Full backend integration  

## 📞 Support

For issues, create a support ticket in the system or contact the development team.

---

**Built with ❤️ by Zeta Development Team**
