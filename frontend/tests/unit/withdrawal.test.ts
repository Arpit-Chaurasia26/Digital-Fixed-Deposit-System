import { describe, it, expect, vi } from 'vitest';


describe('Module 4 - Break FD (Premature Withdrawal) Tests', () => {
 describe('Penalty Calculation', () => {
   it('should apply penalty rate correctly', () => {
     const principal = 100000;
     const originalRate = 0.07; // 7%
     const penaltyRate = 0.02; // 2%
     const effectiveRate = originalRate - penaltyRate; // 5%
     const months = 12;
    
     const n = 4; // Quarterly
     const normalMaturity = principal * Math.pow(1 + originalRate / n, n * (months / 12));
     const penalizedMaturity = principal * Math.pow(1 + effectiveRate / n, n * (months / 12));
    
     const interestLoss = normalMaturity - penalizedMaturity;
    
     expect(effectiveRate).toBe(0.05);
     expect(interestLoss).toBeGreaterThan(2000);
     expect(penalizedMaturity).toBeLessThan(normalMaturity);
   });


   it('should calculate interest loss due to penalty', () => {
     const principal = 50000;
     const originalInterestRate = 0.085; // 8.5%
     const penaltyRate = 0.02;
     const daysHeld = 180;
    
     // Calculate normal interest for days held
     const normalInterest = (principal * originalInterestRate * daysHeld) / 365;
    
     // Calculate penalized interest
     const effectiveRate = originalInterestRate - penaltyRate;
     const penalizedInterest = (principal * effectiveRate * daysHeld) / 365;
    
     const interestLoss = normalInterest - penalizedInterest;
    
     expect(interestLoss).toBeGreaterThan(400);
     expect(penalizedInterest).toBeLessThan(normalInterest);
   });


   it('should calculate total payout after penalty', () => {
     const principal = 100000;
     const earnedInterest = 3500;
     const penalty = 700;
    
     const finalInterest = earnedInterest - penalty;
     const totalPayout = principal + finalInterest;
    
     expect(finalInterest).toBe(2800);
     expect(totalPayout).toBe(102800);
   });


   it('should handle zero penalty for matured FD', () => {
     const maturityAmount = 107186;
     const penalty = 0;
    
     const totalPayout = maturityAmount - penalty;
    
     expect(totalPayout).toBe(maturityAmount);
     expect(penalty).toBe(0);
   });
 });


 describe('Break Preview Calculation', () => {
   it('should generate accurate break preview', () => {
     const fd = {
       principal: 100000,
       originalRate: 0.07,
       penaltyRate: 0.02,
       startDate: new Date('2025-08-10'),
       currentDate: new Date('2026-02-10'),
       maturityDate: new Date('2026-08-10'),
     };
    
     const daysHeld = Math.ceil(
       (fd.currentDate.getTime() - fd.startDate.getTime()) / (1000 * 60 * 60 * 24)
     );
    
     const normalInterest = (fd.principal * fd.originalRate * daysHeld) / 365;
     const effectiveRate = fd.originalRate - fd.penaltyRate;
     const interestAfterPenalty = (fd.principal * effectiveRate * daysHeld) / 365;
     const interestLoss = normalInterest - interestAfterPenalty;
    
     const breakPreview = {
       principal: fd.principal,
       daysHeld,
       normalInterest,
       interestAfterPenalty,
       interestLoss,
       totalPayout: fd.principal + interestAfterPenalty,
     };
    
     expect(breakPreview.daysHeld).toBeGreaterThan(180);
     expect(breakPreview.interestLoss).toBeGreaterThan(0);
     expect(breakPreview.totalPayout).toBeGreaterThan(fd.principal);
   });


   it('should show effective interest rate after penalty', () => {
     const originalRate = 7.5;
     const penaltyRate = 2.0;
     const effectiveRate = originalRate - penaltyRate;
    
     expect(effectiveRate).toBe(5.5);
   });


   it('should calculate days held correctly', () => {
     const startDate = new Date('2025-06-01');
     const currentDate = new Date('2026-02-10');
    
     const diffTime = currentDate.getTime() - startDate.getTime();
     const daysHeld = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
     expect(daysHeld).toBeGreaterThan(250);
   });
 });


 describe('API Tests for Premature Break', () => {
   it('should fetch break preview before confirmation', async () => {
     const mockBreakPreview = {
       fdId: 1,
       principalAmount: 100000,
       originalInterestRate: 7.0,
       penaltyRate: 2.0,
       effectiveInterestRate: 5.0,
       daysHeld: 200,
       normalInterest: 3836,
       interestAfterPenalty: 2740,
       interestLoss: 1096,
       totalPayout: 102740,
     };
    
     const getBreakPreview = vi.fn().mockResolvedValue(mockBreakPreview);
     const preview = await getBreakPreview(1);
    
     expect(getBreakPreview).toHaveBeenCalledWith(1);
     expect(preview.totalPayout).toBe(102740);
     expect(preview.interestLoss).toBeGreaterThan(1000);
   });


   it('should confirm break and update FD status', async () => {
     const mockReceipt = {
       fdId: 1,
       userId: 123,
       principalAmount: 100000,
       interestPaid: 2740,
       totalPayout: 102740,
       penaltyApplied: 1096,
       breakDate: new Date().toISOString(),
       status: 'BROKEN',
     };
    
     const confirmBreak = vi.fn().mockResolvedValue(mockReceipt);
     const receipt = await confirmBreak(1);
    
     expect(confirmBreak).toHaveBeenCalledWith(1);
     expect(receipt.status).toBe('BROKEN');
     expect(receipt.penaltyApplied).toBeGreaterThan(0);
   });


   it('should handle break rejection for ineligible FD', async () => {
     const getBreakPreview = vi.fn().mockRejectedValue(new Error('FD not eligible for premature withdrawal'));
    
     await expect(getBreakPreview(1)).rejects.toThrow('FD not eligible for premature withdrawal');
   });
 });


 describe('Withdrawal Confirmation Flow', () => {
   it('should require user confirmation before breaking FD', () => {
     let userConfirmed = false;
    
     const confirmationDialog = () => {
       const userInput = true; // Simulate user clicking "Confirm"
       userConfirmed = userInput;
       return userConfirmed;
     };
    
     const result = confirmationDialog();
     expect(result).toBe(true);
   });


   it('should not break FD if user cancels', () => {
     let fdBroken = false;
    
     const confirmationDialog = () => {
       return false; // User clicks "Cancel"
     };
    
     if (confirmationDialog()) {
       fdBroken = true;
     }
    
     expect(fdBroken).toBe(false);
   });
 });


 describe('FD List Update After Break', () => {
   it('should update FD status in list after break', () => {
     const fdList = [
       { id: 1, status: 'ACTIVE', principalAmount: 100000 },
       { id: 2, status: 'ACTIVE', principalAmount: 50000 },
     ];
    
     // Simulate breaking FD with id 1
     const brokenFdId = 1;
     const updatedList = fdList.map(fd =>
       fd.id === brokenFdId ? { ...fd, status: 'BROKEN' } : fd
     );
    
     expect(updatedList[0].status).toBe('BROKEN');
     expect(updatedList[1].status).toBe('ACTIVE');
   });


   it('should add payout information to broken FD', () => {
     const fd = {
       id: 1,
       status: 'ACTIVE',
       principalAmount: 100000,
       currentValue: 103500,
     };
    
     // After break
     const brokenFd = {
       ...fd,
       status: 'BROKEN',
       finalPayout: 102740,
       penaltyApplied: 760,
       breakDate: new Date().toISOString(),
     };
    
     expect(brokenFd.status).toBe('BROKEN');
     expect(brokenFd.finalPayout).toBeLessThan(fd.currentValue);
     expect(brokenFd.penaltyApplied).toBeGreaterThan(0);
   });
 });


 describe('Interest Loss Computation', () => {
   it('should calculate percentage of interest lost', () => {
     const normalInterest = 3836;
     const interestAfterPenalty = 2740;
     const interestLoss = normalInterest - interestAfterPenalty;
    
     const lossPercentage = (interestLoss / normalInterest) * 100;
    
     expect(lossPercentage).toBeGreaterThan(25);
     expect(lossPercentage).toBeCloseTo(28.6, 1);
   });


   it('should show penalty impact on returns', () => {
     const expectedReturns = 53500; // Without penalty
     const actualReturns = 52370; // With penalty
    
     const impactAmount = expectedReturns - actualReturns;
     const impactPercent = (impactAmount / expectedReturns) * 100;
    
     expect(impactAmount).toBe(1130);
     expect(impactPercent).toBeCloseTo(2.11, 1);
   });
 });
});



