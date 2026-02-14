import { describe, it, expect } from 'vitest';
import * as userViews from '@/views/user';

describe('user views barrel exports', () => {
  it('exports all expected user views', () => {
    expect(userViews.UserDashboard).toBeTruthy();
    expect(userViews.FDList).toBeTruthy();
    expect(userViews.FDDetails).toBeTruthy();
    expect(userViews.FDInterest).toBeTruthy();
    expect(userViews.BreakFD).toBeTruthy();
    expect(userViews.Portfolio).toBeTruthy();
    expect(userViews.SupportTickets).toBeTruthy();
    expect(userViews.CreateTicket).toBeTruthy();
  });
});
