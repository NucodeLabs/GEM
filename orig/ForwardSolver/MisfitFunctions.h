#pragma once

//! ������ �������������� ����������
double CalcRelativeDeviation(const double r_exp,const double r_teor);
//! ������ �������������� ����������, �������������� �� ����������� ���������
double CalcRelativeDeviationWithError(const double r_exp,const double r_exp_err, const double r_teor);
//! ������ ���������� ������������� �� ����������� ���� � ����������
double CalcRok(const double AB,const double MN,const double U,const double I);
//! ������ � ������ ����������� ���������
void CalcRokWithError(const double AB, const double MN, const double distA_err, const double distB_err,
	const double U,const double AU_err, const double BU_err, 
	const double I, const double AI_err, const double BI_err, 
	double* Rok,double* Rok_err);

