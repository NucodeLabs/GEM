#pragma once

//! Расчет относительного отклонения
double CalcRelativeDeviation(const double r_exp,const double r_teor);
//! Расчет относительного отклонения, нормированного на погрешность измерения
double CalcRelativeDeviationWithError(const double r_exp,const double r_exp_err, const double r_teor);
//! Расчет кажущегося сопротивления по измеренному току и напряжению
double CalcRok(const double AB,const double MN,const double U,const double I);
//! Расчет с учетом погрешности измерения
void CalcRokWithError(const double AB, const double MN, const double distA_err, const double distB_err,
	const double U,const double AU_err, const double BU_err, 
	const double I, const double AI_err, const double BI_err, 
	double* Rok,double* Rok_err);

