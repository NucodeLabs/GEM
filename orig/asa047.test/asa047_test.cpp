#include "pch.h"
#include "asa047.h"

double nelmin_fn_test_polynom (const double x[], const void *pdata) {
	const double *coeff = (const double *)pdata;
	const double cur_val = x[0];
	return coeff[0]* cur_val*cur_val + coeff[1]* cur_val + coeff[2];
}

TEST(asa047, run) {
	const int count_x = 1;
	double start[] = { -5 };
	double step[] = { 1 };
	double xmin[] = { 0};  //! (out) найденный минимум функции
	int numres = 0;		 //! Количество рестартов
	double YNEWLO = 0;		 //! (out) Итоговая достигнутая точность
	double reqmin = 1e-6;    //! Требуемый минимум 
	int kcount = 100;    //! Максимальное количество итераций
	int KONVGE = 1;		 //! Максимально количество рестартов
	int icount = 0;      //! (out) Количество выполненных итераций
	int ifault = 0;      //! (out) Причина завершения алгоритма
	const double pdata[] = { 2,4,8 };
	
	nelmin(&nelmin_fn_test_polynom, count_x, start, xmin, &YNEWLO, reqmin, step, KONVGE, kcount, &icount, &numres, &ifault,pdata);

    EXPECT_NEAR(xmin[0], -1,1e-6);
}

TEST(asa047, fix) {
	const double prec = 1e-6;

	const int count_x = 1;
	double start[] = { -5 };
	double step[] = { 1 };
	double xmin[] = { 0 };  //! (out) найденный минимум функции
	int numres = 0;		 //! Количество рестартов
	double YNEWLO = 0;		 //! (out) Итоговая достигнутая точность
	double reqmin = 1e-6;    //! Требуемый минимум 
	int kcount = 100;    //! Максимальное количество итераций
	int KONVGE = 1;		 //! Максимально количество рестартов
	int icount = 0;      //! (out) Количество выполненных итераций
	int ifault = 0;      //! (out) Причина завершения алгоритма
	const double pdata[] = { 2,4,8 };

	int fix[] = { FIX_PARAM };
	nelmin_fix(&nelmin_fn_test_polynom, count_x, start, fix, xmin, &YNEWLO, reqmin, step, KONVGE, kcount, &icount, &numres, &ifault, pdata);

	EXPECT_NEAR(xmin[0], start[0], prec);

	fix[0] = UNFIX_PARAM;
	nelmin_fix(&nelmin_fn_test_polynom, count_x, start, fix, xmin, &YNEWLO, reqmin, step, KONVGE, kcount, &icount, &numres, &ifault, pdata);

	EXPECT_NEAR(xmin[0], -1, prec);
}

double nelmin_fn_test_fix(const double x[], const void *pdata) {
	return pow(x[0],2) + pow(x[1],2);
}

TEST(asa047, fix1) {
	const int count_x = 3;
	double start[] = { -5, 0, 5 };
	double step[] = { 1, 1, 1 };
	double xmin[] = { 0, 0, 0 };  //! (out) найденный минимум функции
	double xmin_fix[] = { 0, 0, 0 };
	int numres = 0;		 //! Количество рестартов
	double YNEWLO = 0;		 //! (out) Итоговая достигнутая точность
	double reqmin = 1e-6;    //! Требуемый минимум 
	int kcount = 100;    //! Максимальное количество итераций
	int KONVGE = 1;		 //! Максимально количество рестартов
	int icount = 0;      //! (out) Количество выполненных итераций
	int ifault = 0;      //! (out) Причина завершения алгоритма
	const double pdata[] = { 2,4,8 };

	nelmin(&nelmin_fn_test_fix, count_x, start, xmin, &YNEWLO, reqmin, step, KONVGE, kcount, &icount, &numres, &ifault, pdata);
	int fix[] = { UNFIX_PARAM, UNFIX_PARAM, FIX_PARAM };
	double start_fix[] = { -5, 7, 5 };
	nelmin_fix(&nelmin_fn_test_fix, count_x, start_fix, fix, xmin_fix, &YNEWLO, reqmin, step, KONVGE, kcount, &icount, &numres, &ifault, pdata);
	
	EXPECT_NEAR(xmin[0], xmin_fix[0], 1e-1);
	EXPECT_NEAR(xmin[1], xmin_fix[1], 1e-1);
	EXPECT_NEAR(5, xmin_fix[2], 1e-1);
}
