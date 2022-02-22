#pragma once

typedef double(*nelmin_fn)(const double x[], const void *pdata);

void nelmin(nelmin_fn fn, int n, double start[], double xmin[],
	double *ynewlo, double reqmin, double step[], int konvge, int kcount,
	int *icount, int *numres, int *ifault, const void* pdata);

#define FIX_PARAM 1
#define UNFIX_PARAM 0
#define INCORRECT_FIX_IND 3
#define STEP_SIZE_NOT_EQUALS 4

void nelmin_fix(nelmin_fn fn, int n, double start[], int fix[], double xmin[],
	double *ynewlo, double reqmin, double step[], int konvge, int kcount,
	int *icount, int *numres, int *ifault, const void* pdata);

//Расчет чувствительности сигнала к подобранным моделям
void ResponseFunction(const nelmin_fn fn, const void* pdata,const int n,const double xmin[],const double eps,const double xmin_border_init[],const double xmax_border_init[], double xmin_border[], double xmax_border[]);


void gauss_fix(nelmin_fn fn, int n, double start[], int fix[], double xmin[],
	double *ynewlo, double reqmin, double step[], int konvge, int kcount,
	int *icount, int *numres, int *ifault, const void* pdata);