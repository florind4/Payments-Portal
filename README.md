# Centralized Mobile Payment Portal

A comprehensive mobile payment solution featuring a centralized portal with 2 integrated provider applications, built with React Native, Expo, Spring Boot, and PostgreSQL.

## 🏗️ Architecture Overview

This project implements a **multi-tenant payment ecosystem** consisting of:

- **Central Portal App**: Main payment hub for end users
- **Provider Apps**: Two independent provider applications (First Provider & Second Provider)
- **Shared Database**: PostgreSQL database with multi-tenant architecture
- **Mobile Frontend**: React Native/Expo applications
- **Backend Services**: Spring Boot REST APIs with JWT authentication

## 📱 Applications

### 1. Central Portal App (`portal-app/`)
**Primary payment hub for end users**

**Backend Features:**
- Spring Boot 2.7.0 with Java 11
- JWT-based authentication and authorization
- RESTful API endpoints for bills, payments, and user management
- Integration with Twilio for SMS notifications
- Email service integration (Brevo SMTP)
- Multi-currency support (RON default)
- PDF generation for bills
- Real-time notifications

**Frontend Features:**
- React Native with Expo SDK 49
- Dark/Light theme support
- Bill management and payment processing
- User profile management
- Provider connection management
- Admin panel for user management
- Payment method selection (Card/Bank Transfer)
- Bill filtering and search capabilities

**Key Services:**
- Authentication & Authorization
- Bill Management
- Payment Processing
- User Profile Management
- Provider Integration
- Notification System
- PDF Generation
- Cache Management

### 2. First Provider App (`first-provider-app/`)
**Independent provider application with developer portal**

**Features:**
- Developer account management
- Bill creation and management
- Transaction processing
- WebSocket support for real-time updates
- Provider-specific authentication
- Developer dashboard

### 3. Second Provider App (`second-provider-app/`)
**Alternative provider application with similar capabilities**

**Features:**
- Independent provider ecosystem
- Developer management
- Bill processing
- Transaction tracking
- Provider-specific features

## 🗄️ Database Schema

### Multi-Tenant Architecture
The PostgreSQL database implements a multi-tenant design with separate tables for each application:

**Portal Tables:**
- `utilizatori` - User accounts
- `facturi` - Bills
- `tranzactii` - Transactions
- `conexiuni` - Provider connections

**Provider Tables:**
- `utilizatorif1/f2` - Provider-specific users
- `facturif1/f2` - Provider-specific bills
- `tranzactiif1/f2` - Provider-specific transactions
- `conexiunif1/f2` - Provider connections
- `developers/developers2` - Developer accounts
- `scheduled_bills/scheduled_bills2` - Scheduled payments

### Key Database Features:
- **Multi-tenant isolation** with separate tables per provider
- **Connection management** between portal and providers
- **Scheduled payments** support
- **Notification tracking** for bills
- **Audit trails** with timestamps
- **Currency support** (RON default)
- **Admin role management**

## 🚀 Key Features

### 🔐 Authentication & Security
- JWT token-based authentication
- Password encryption with BCrypt
- Role-based access control (Admin/User)
- Secure API endpoints with CORS configuration
- Session management with AsyncStorage

### 💳 Payment Processing
- Multiple payment methods (Card, Bank Transfer)
- Real-time payment status updates
- Transaction history tracking
- Payment confirmation and receipts
- Currency conversion support

### 📋 Bill Management
- Bill creation and management
- Due date tracking and notifications
- Bill status monitoring (Paid/Unpaid)
- Bill filtering by amount and provider
- PDF generation for bills
- Scheduled payment setup

### 👥 User Management
- User registration and profile management
- Admin panel for user oversight
- Profile photo upload
- Address and contact information
- Currency preferences
- Balance tracking

### 🔗 Provider Integration
- Provider connection management
- Multi-provider support
- Provider-specific authentication
- Developer portal access
- Provider API integration

### 📱 Mobile Features
- Cross-platform React Native app
- Offline capability with local storage
- Push notifications
- Dark/Light theme support
- Responsive design
- Native device features (camera, file system)

### 🔔 Notifications
- SMS notifications via Twilio
- Email notifications via Brevo
- Push notifications for bills
- Payment confirmations
- Due date reminders

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 2.7.0
- **Language**: Java 11
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Notifications**: Twilio SMS, Brevo Email
- **PDF**: HTML to PDF conversion

### Frontend
- **Framework**: React Native with Expo
- **Language**: TypeScript
- **Navigation**: React Navigation 6
- **UI Components**: React Native Paper
- **State Management**: React Context + AsyncStorage
- **HTTP Client**: Fetch API
- **Icons**: Expo Vector Icons
- **Notifications**: Expo Notifications

### Database
- **RDBMS**: PostgreSQL
- **Connection Pool**: HikariCP
- **Migration**: Flyway (planned)
- **Multi-tenancy**: Schema-based separation

### Development Tools
- **IDE**: VS Code recommended
- **API Testing**: Postman/Insomnia
- **Database**: pgAdmin/DBeaver
- **Mobile Testing**: Expo Go app

## 📦 Installation & Setup

### Prerequisites
- Java 11 or higher
- Node.js 16+ and npm
- PostgreSQL 12+
- Android Studio (for Android development)
- Xcode (for iOS development, macOS only)

### Database Setup
1. Install PostgreSQL
2. Create database: `postgres`
3. Create user: `florin1` with password: `florin1`
4. Run database schema from `database/database.txt`

### Backend Setup

#### Portal App Backend
```bash
cd portal-app/the-app/backend
mvn clean install
mvn spring-boot:run
```

#### Provider App Backends
```bash
# First Provider
cd first-provider-app/the-app/backend
mvn clean install
mvn spring-boot:run

# Second Provider
cd second-provider-app/the-app/backend
mvn clean install
mvn spring-boot:run
```

### Frontend Setup

#### Portal App Frontend
```bash
cd portal-app/the-app/frontend/mobile-app-expo
npm install
npx expo start
```

#### Provider App Frontends
```bash
# First Provider
cd first-provider-app/the-app/frontend/mobile-app-expo
npm install
npx expo start

# Second Provider
cd second-provider-app/the-app/frontend/mobile-app-expo
npm install
npx expo start
```

## 🚀 Deployment

### Backend Deployment
1. Build JAR files: `mvn clean package`
2. Deploy to application server (Tomcat, etc.)
3. Configure environment variables
4. Set up reverse proxy (Nginx)

### Frontend Deployment
1. Build APK/IPA: `expo build:android` / `expo build:ios`
2. Deploy to app stores or internal distribution
3. Configure production API endpoints

### Database Deployment
1. Set up PostgreSQL server
2. Configure connection pooling
3. Set up automated backups
4. Configure monitoring and logging

## 📱 Mobile App Features

### Screens & Navigation
- **Welcome Screen**: App introduction
- **Login/Register**: User authentication
- **Home Screen**: Dashboard with quick actions
- **Bills Screen**: Bill management and payment
- **Profile Screen**: User profile management
- **Payment Screens**: Card and bank transfer options
- **Admin Screens**: User management (admin only)

### Key Components
- **BillFilter**: Advanced bill filtering
- **CountrySelector**: Country selection for payments
- **ConfirmationDialog**: Payment confirmations
- **ThemeContext**: Dark/Light theme management

### Services
- **Authentication**: Login, registration, token management
- **Bill Management**: CRUD operations, filtering, payment
- **Payment Processing**: Multiple payment methods
- **Notifications**: Push, SMS, email notifications
- **Profile Management**: User data and preferences
- **Provider Integration**: Provider connection management

## 🔒 Security Features

### Authentication
- JWT token-based authentication
- Password hashing with BCrypt
- Token expiration and refresh
- Secure storage with AsyncStorage

### API Security
- CORS configuration
- Input validation
- SQL injection prevention
- XSS protection

### Data Protection
- Encrypted sensitive data
- Secure API communication
- Role-based access control
- Audit logging

## 📊 Monitoring & Logging

### Backend Logging
- SLF4J with Logback
- Structured logging
- Error tracking
- Performance monitoring

### Frontend Logging
- Console logging for debugging
- Error boundary implementation
- Network request logging
- User interaction tracking

## 🧪 Testing

### Backend Testing
- Unit tests with JUnit
- Integration tests
- API endpoint testing
- Security testing

### Frontend Testing
- Component testing
- Navigation testing
- API integration testing
- User flow testing

## 📈 Performance Optimization

### Backend
- Connection pooling (HikariCP)
- JPA batch operations
- Caching strategies
- Database indexing

### Frontend
- Image optimization
- Lazy loading
- Memory management
- Network request optimization

## 🔄 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Bills
- `GET /api/bills` - Get user bills
- `POST /api/bills` - Create bill
- `PUT /api/bills/{id}` - Update bill
- `DELETE /api/bills/{id}` - Delete bill
- `POST /api/bills/{id}/pay` - Pay bill

### Users
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update profile
- `GET /api/users/admin` - Admin user management

### Payments
- `POST /api/payments/card` - Card payment
- `POST /api/payments/bank-transfer` - Bank transfer
- `GET /api/payments/history` - Payment history

## 🤝 Contributing

### Development Workflow
1. Fork the repository
2. Create feature branch
3. Implement changes
4. Add tests
5. Submit pull request

### Code Standards
- Follow Java coding conventions
- Use TypeScript for frontend
- Implement proper error handling
- Add comprehensive documentation
- Write unit tests

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

### Common Issues
1. **Database Connection**: Check PostgreSQL service and credentials
2. **API Connection**: Verify backend is running and ports are correct
3. **Mobile Build**: Ensure Expo CLI is installed and configured
4. **Authentication**: Check JWT configuration and token expiration

### Getting Help
- Check the documentation
- Review error logs
- Test API endpoints
- Verify configuration files

## 🔮 Future Enhancements

### Planned Features
- **Real-time Chat**: Customer support integration
- **Advanced Analytics**: Payment trends and insights
- **Multi-language Support**: Internationalization
- **Biometric Authentication**: Fingerprint/Face ID
- **Blockchain Integration**: Cryptocurrency payments
- **AI-powered Features**: Smart bill categorization
- **Advanced Notifications**: Custom notification preferences
- **Offline Mode**: Enhanced offline capabilities

### Technical Improvements
- **Microservices Architecture**: Service decomposition
- **Containerization**: Docker deployment
- **CI/CD Pipeline**: Automated testing and deployment
- **Performance Monitoring**: APM integration
- **Security Auditing**: Regular security assessments

---

**Built using React Native, Expo, Spring Boot, and PostgreSQL** 