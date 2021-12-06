#define _USE_MATH_DEFINES
#include <math.h>
#include "MisfitFunctions.h"

double CalcRelativeDeviation(const double r_exp, const double r_teor) {
	if (r_exp <= 0 || r_teor <= 0)
		return NAN;
	return (r_exp - r_teor) / r_exp;	
}

double CalcRelativeDeviationWithError(const double r_exp, const double r_exp_err, const double r_teor) {
	if (r_exp <= 0 || r_teor <= 0)
		return NAN;
	return (r_exp - r_teor) / (r_exp_err*r_exp);
}

double FixMinValue(const double val) {
	return val > 0 ? val : 0;
}
//A-M-O-N-B
double CalcRok(const double AB, const double MN, const double U, const double I) {
	if (AB <= 0 || MN <= 0 || U <= 0 || I <= 0)
		return NAN;
	const auto AB2 = AB / 2, MN2 = MN / 2;
    auto AM = AB2 - MN2, BM = AB2 + MN2;
	AM=FixMinValue(AM);
	const auto AN = BM, BN = AM;
	const double k = 2 * M_PI / (1/AM-1/BM-1/AN+1/BN);
	const double res = k * U / I;
	return res;
}

void CalcMinMaxWithError(const double val,const double a_err,const double b_err, double* min_val,double* max_val) {
	*min_val = val - val * a_err - b_err;
	if (*min_val < 0)
		*min_val = 0;
	*max_val = val + val * a_err + b_err;
}

void CalcRokWithError(const double AB, const double MN, const double distA_err, const double distB_err,
	const double U, const double AU_err, const double BU_err,
	const double I, const double AI_err, const double BI_err,
	double* Rok, double* Rok_err) {
	// Расчет среднего кажущегося сопротивления
	const double avg_rok = CalcRok(AB,MN,U,I);
	// Расчёт расстояний
	double min_AB2, max_AB2, min_MN2, max_MN2;
	CalcMinMaxWithError(AB/2, distA_err, distB_err,&min_AB2,&max_AB2);
	CalcMinMaxWithError(MN / 2, distA_err, distB_err, &min_MN2, &max_MN2);
	auto min_AM = min_AB2 - max_MN2, min_BM = min_AB2 + min_MN2;
	min_AM = FixMinValue(min_AM);
	auto max_AM = max_AB2 - min_MN2, max_BM = max_AB2 + max_MN2;
	max_AM = FixMinValue(max_AM);
	const auto min_AN = min_BM, min_BN = min_AM;
	const auto max_AN = max_BM, max_BN = max_AM;

	double min_U, max_U, min_I, max_I;
	CalcMinMaxWithError(I, AI_err, BI_err, &min_I, &max_I);
	CalcMinMaxWithError(U, AU_err, BU_err, &min_U, &max_U);
	

	double min_k = 2 * M_PI / (1 / min_AM - 1 / max_BM - 1 / max_AN + 1 / min_BN);
	double max_k = 2 * M_PI / (1 / max_AM - 1 / min_BM - 1 / min_AN + 1 / max_BN);

	double min_rok = min_k * min_U / max_I;
	min_rok = min_rok > 0 ? min_rok : 0;
	double max_rok = max_k * max_U / min_I;

	const auto min_err = (avg_rok - min_rok) / avg_rok;
	const auto max_err = (max_rok - avg_rok) / avg_rok;

	*Rok = avg_rok;
	*Rok_err = fmax(fabs(min_err), fabs(max_err));
}