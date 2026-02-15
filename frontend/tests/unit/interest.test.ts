import { describe, it, expect } from 'vitest';

describe('Module 3 - FD Interest and Maturity Tests', () => {
  describe('Interest Calculation Logic', () => {
    it('should calculate monthly interest accrual correctly', () => {
      const principal = 100000;
      const annualRate = 0.07; // 7%
      const months = 12;
      
      // Monthly compound interest
      const monthlyRate = annualRate / 12;
      const maturity = principal * Math.pow(1 + monthlyRate, months);
      
      const totalInterest = maturity - principal;
      
      expect(totalInterest).toBeGreaterThan(7000);
      expect(totalInterest).toBeLessThan(7500);
      expect(maturity).toBeCloseTo(107229, 0);
    });

    it('should calculate quarterly interest accrual correctly', () => {
      const principal = 100000;
      const annualRate = 0.07;
      const quarters = 4; // 1 year = 4 quarters
      
      // Quarterly compound interest
      const quarterlyRate = annualRate / 4;
      const maturity = principal * Math.pow(1 + quarterlyRate, quarters);
      
      const totalInterest = maturity - principal;
      
      expect(totalInterest).toBeCloseTo(7186, 0);
      expect(maturity).toBeCloseTo(107186, 0);
    });

    it('should calculate yearly interest accrual correctly', () => {
      const principal = 100000;
      const annualRate = 0.07;
      const years = 1;
      
      // Annual compound interest
      const maturity = principal * (1 + annualRate * years);
      
      const totalInterest = maturity - principal;
      
      expect(totalInterest).toBe(7000);
      expect(maturity).toBe(107000);
    });

    it('should calculate interest for multiple years', () => {
      const principal = 100000;
      const annualRate = 0.07;
      const years = 5;
      const n = 4; // Quarterly
      
      const maturity = principal * Math.pow(1 + annualRate / n, n * years);
      
      expect(maturity).toBeGreaterThan(140000);
      expect(maturity).toBeCloseTo(141478, 0);
    });
  });

  describe('Interest Timeline Generation', () => {
    it('should generate monthly interest timeline', () => {
      const principal = 50000;
      const rate = 0.075; // 7.5%
      const months = 12;
      
      const timeline = [];
      let cumulativeInterest = 0;
      
      for (let month = 1; month <= months; month++) {
        const currentValue = principal * Math.pow(1 + rate / 12, month);
        const monthInterest: number = currentValue - (timeline.length > 0 ? timeline[timeline.length - 1].totalValue : principal);
        cumulativeInterest += monthInterest;
        
        timeline.push({
          month,
          interestAccrued: monthInterest,
          cumulativeInterest,
          totalValue: currentValue,
        });
      }
      
      expect(timeline.length).toBe(12);
      expect(timeline[0].month).toBe(1);
      expect(timeline[11].cumulativeInterest).toBeGreaterThan(3500);
    });

    it('should generate quarterly interest timeline', () => {
      const principal = 100000;
      const rate = 0.08;
      const quarters = 4;
      
      const timeline = [];
      
      for (let q = 1; q <= quarters; q++) {
        const currentValue = principal * Math.pow(1 + rate / 4, q);
        const quarterInterest: number = currentValue - (q === 1 ? principal : timeline[q - 2].totalValue);
        
        timeline.push({
          quarter: q,
          interestAccrued: quarterInterest,
          totalValue: currentValue,
        });
      }
      
      expect(timeline.length).toBe(4);
      expect(timeline[3].totalValue).toBeGreaterThan(108000);
    });
  });

  describe('FD Status Updates', () => {
    it('should update FD status on mock maturity check', () => {
      const fd = {
        id: 1,
        maturityDate: new Date('2026-02-01').toISOString(),
        status: 'ACTIVE',
      };
      
      const currentDate = new Date('2026-02-10');
      const maturityDate = new Date(fd.maturityDate);
      
      if (currentDate >= maturityDate) {
        fd.status = 'MATURED';
      }
      
      expect(fd.status).toBe('MATURED');
    });

    it('should keep FD as ACTIVE if not matured', () => {
      const fd = {
        id: 1,
        maturityDate: new Date('2027-12-31').toISOString(),
        status: 'ACTIVE',
      };
      
      const currentDate = new Date('2026-02-10');
      const maturityDate = new Date(fd.maturityDate);
      
      if (currentDate >= maturityDate) {
        fd.status = 'MATURED';
      }
      
      expect(fd.status).toBe('ACTIVE');
    });

    it('should calculate days until maturity', () => {
      const maturityDate = new Date('2026-03-10');
      const currentDate = new Date('2026-02-10');
      
      const diffTime = maturityDate.getTime() - currentDate.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      expect(diffDays).toBe(28);
    });

    it('should identify FDs maturing within 7 days', () => {
      const fds = [
        { id: 1, maturityDate: new Date('2026-02-12').toISOString() }, // 2 days
        { id: 2, maturityDate: new Date('2026-02-20').toISOString() }, // 10 days
        { id: 3, maturityDate: new Date('2026-02-15').toISOString() }, // 5 days
      ];
      
      const currentDate = new Date('2026-02-10');
      const daysThreshold = 7;
      
      const maturingSoon = fds.filter(fd => {
        const maturityDate = new Date(fd.maturityDate);
        const diffTime = maturityDate.getTime() - currentDate.getTime();
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays >= 0 && diffDays <= daysThreshold;
      });
      
      expect(maturingSoon.length).toBe(2);
      expect(maturingSoon.map(fd => fd.id)).toEqual([1, 3]);
    });
  });

  describe('Maturity Badge Logic', () => {
    it('should show MATURED badge for matured FD', () => {
      const fd = {
        status: 'MATURED',
        maturityDate: new Date('2026-01-01').toISOString(),
      };
      
      const badge = fd.status === 'MATURED' ? 'MATURED' : 'ACTIVE';
      
      expect(badge).toBe('MATURED');
    });

    it('should show ACTIVE badge for active FD', () => {
      const fd = {
        status: 'ACTIVE',
        maturityDate: new Date('2027-12-31').toISOString(),
      };
      
      const badge = fd.status === 'MATURED' ? 'MATURED' : 'ACTIVE';
      
      expect(badge).toBe('ACTIVE');
    });

    it('should show MATURING SOON badge for FDs close to maturity', () => {
      const fd = {
        status: 'ACTIVE',
        maturityDate: new Date('2026-02-15').toISOString(),
      };
      
      const currentDate = new Date('2026-02-10');
      const maturityDate = new Date(fd.maturityDate);
      const diffDays = Math.ceil((maturityDate.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24));
      
      const badge = diffDays <= 7 && diffDays > 0 ? 'MATURING_SOON' : fd.status;
      
      expect(badge).toBe('MATURING_SOON');
    });
  });

  describe('Interest Breakdown Display', () => {
    it('should show principal and interest separately', () => {
      const principal = 100000;
      const maturityAmount = 107186;
      const totalInterest = maturityAmount - principal;
      
      const breakdown = {
        principal,
        interest: totalInterest,
        total: maturityAmount,
      };
      
      expect(breakdown.principal).toBe(100000);
      expect(breakdown.interest).toBeCloseTo(7186, 0);
      expect(breakdown.total).toBeCloseTo(107186, 0);
    });

    it('should calculate interest percentage of total', () => {
      const principal = 50000;
      const interest = 3500;
      const total = principal + interest;
      
      const interestPercentage = (interest / total) * 100;
      
      expect(interestPercentage).toBeCloseTo(6.54, 1);
    });
  });
});
