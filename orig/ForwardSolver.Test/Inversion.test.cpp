#include "pch.h"
#include "../ForwardSolver/Inversion.h"
#include "VesData.h"

TEST(Inversion, StartModelDebug) {
	int32_t Nlay = 10;
	double Rom[10];
	double Hm[10];

	int32_t code_err= StartModelSimple(&Nlay, Rom, Hm,
		size1, AB1, Rok1, 0);
	/*
	const double AB = 2 * 15, MN = 2 * 3;
	const double U = 41 * 0.001;
	const double I = 100 * 0.001;
	double Rok = CalcRok(AB, MN, U, I);
	*/
}