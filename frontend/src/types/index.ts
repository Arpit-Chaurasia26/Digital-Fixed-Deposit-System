// ============================================
// TYPE DEFINITIONS FOR THE APPLICATION
// ============================================

// Auth Types
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role?: 'USER' | 'ADMIN';
}

export interface UserProfile {
  id: number;
  name: string;
  email: string;
  role: 'USER' | 'ADMIN';
  accountBalance: number;
}

export interface UpdateProfileRequest {
  name: string;
  email: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

// FD Types
export const FDStatus = {
  ACTIVE: 'ACTIVE',
  MATURED: 'MATURED',
  BROKEN: 'BROKEN',
  PENDING: 'PENDING'
} as const;

export type FDStatus = typeof FDStatus[keyof typeof FDStatus];

export const InterestScheme = {
  STANDARD_6_MONTHS: 'STANDARD_6_MONTHS',
  STANDARD_12_MONTHS: 'STANDARD_12_MONTHS',
  SENIOR_CITIZEN_12_MONTHS: 'SENIOR_CITIZEN_12_MONTHS',
  TAX_SAVER_5_YEARS: 'TAX_SAVER_5_YEARS'
} as const;

export type InterestScheme = typeof InterestScheme[keyof typeof InterestScheme];

export interface InterestSchemeDetails {
  name: string;
  annualInterestRate: number;
  tenureInMonths: number;
  interestFrequency: 'MONTHLY' | 'QUARTERLY' | 'YEARLY';
  prematureBreakAllowed: boolean;
}

export interface FixedDeposit {
  id: number;
  userId: number;
  amount: number;
  interestRate: number;
  tenureMonths: number;
  startDate: string;
  maturityDate: string;
  status: FDStatus;
  interestScheme: InterestScheme;
  accruedInterest?: number;
  createdAt: string;
  updatedAt: string;
}

export interface BookFDRequest {
  amount: number;
  interestScheme: InterestScheme;
}

export interface FDMaturityResponse {
  fdId: number;
  userId: number;
  amount: number;
  maturityDate: string;
  daysRemaining: number;
  status: FDStatus;
}

export interface FDFinancialYearSummary {
  financialYear: string;
  totalFDsCreated: number;
  totalPrincipalDeposited: number;
  totalInterestAccruedTillDate: number;
}

export interface AdminFinancialYearSummary {
  financialYear: string;
  totalFDsCreated: number;
  totalPrincipalDeposited: number;
  totalInterestAccruedTillDate: number;
}

export interface FDPortfolio {
  totalFDs: number;
  activeFDs: number;
  maturedFDs: number;
  brokenFDs: number;
  totalPrincipal: number;
  totalInterestAccrued: number;
  nextMaturityDate?: string;
}

export interface InterestAccrual {
  period: string;
  accruedInterest: number;
}

export interface FDInterestTimeline {
  fdId: number;
  principal: number;
  interestRate: number;
  interval: string;
  timeline: InterestAccrual[];
}

export interface FDInterestResponse {
  fdId: number;
  status: FDStatus;
  amount: number;
  accruedInterest: number;
  interestRate: number;
  interestFrequency: 'MONTHLY' | 'QUARTERLY' | 'YEARLY';
  startDate: string;
  maturityDate: string;
}

// Withdrawal Types
export interface WithdrawalPreview {
  principleAmount: number;
  accumulatedInterestAmount: number;
  interestRate: number;
  penalty: number;
  netInterestAmount: number;
  fdId?: number;
  principalAmount?: number;
  interestAfterPenalty?: number;
  totalPayout?: number;
  originalInterestRate?: number;
  penaltyRate?: number;
  effectiveInterestRate?: number;
  daysHeld?: number;
  normalInterest?: number;
  interestLoss?: number;
  maturityDate?: string;
  breakDate?: string;
}

export interface WithdrawalReceipt {
  fdId: number;
  userId: number;
  principalAmount: number;
  interestPaid: number;
  totalPayout: number;
  penaltyApplied: number;
  breakDate: string;
  originalMaturityDate: string;
  status: string;
}

// Support Ticket Types
export const TicketStatus = {
  OPEN: 'OPEN',
  IN_PROGRESS: 'IN_PROGRESS',
  RESOLVED: 'RESOLVED',
  CLOSED: 'CLOSED'
} as const;

export type TicketStatus = typeof TicketStatus[keyof typeof TicketStatus];

export interface SupportTicketRequest {
  subject: string;
  description: string;
  fdId?: number;
}

export interface SupportTicket {
  id: number;
  userId: number;
  fdId?: number;
  subject: string;
  description: string;
  status: TicketStatus;
  response?: string;
  createdTime: string;
  updatedTime: string;
  userName?: string;
}

export interface SupportTicketPage {
  content: SupportTicket[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// API Response wrapper
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

// FD Schemes for display
export interface FDSchemeCard {
  name: string;
  schemeName: string;
  description: string;
  annualInterestRate: number;
  tenureInMonths: number;
  interestFrequency: 'MONTHLY' | 'QUARTERLY' | 'YEARLY';
  prematureBreakAllowed: boolean;
  features: string[];
  icon: string;
}

// API response for all schemes
export interface SchemeResponse {
  name: string;
  annualInterestRate: number;
  tenureInMonths: number;
  interestFrequency: 'MONTHLY' | 'QUARTERLY' | 'YEARLY';
  prematureBreakAllowed: boolean;
}

//Eligibility Message
export interface EligibilityMessage{
  isEligible:boolean,
  rootCause:string
}

//WithdrawalHistory
export interface WIthdrawalHistory{
  fdId:number,
  withdrawalAmount:number,
  intrestGained:number,
  withdrawnDate:string
}
