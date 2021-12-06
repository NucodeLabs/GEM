#include "pch.h"
#include "../ForwardSolver/MisfitFunctions.h"
#include "VesData.h"

TEST(CalcRok, Debug) {
	const double AB = 2 * 15, MN = 2 * 3;
	const double U = 41* 0.001;
	const double I = 100 * 0.001;
	double Rok=CalcRok(AB, MN, U,  I);
}


TEST(CalcRokWithError, Debug) {
  double AB = 2 * 15, MN = 2 * 3;
  const double distA_err = 0.001, distB_err = 0.001;
  double U = 126.4 * 0.001;
  const double AU_err=0.01, BU_err=0.001;
  double I = 100 * 0.001;
  const double AI_err= 0.01, BI_err=0.001;  
  double Rok_p2, Rok_err_p2;
  CalcRokWithError(AB, MN, distA_err, distB_err, U, AU_err, BU_err, I, AI_err, BI_err, &Rok_p2,&Rok_err_p2);
  
  AB = 2 * 15, MN = 2 * 0.5;
  U = 17.38 * 0.001;
  I = 100 * 0.001;
  double Rok_p1, Rok_err_p1;
  CalcRokWithError(AB, MN, distA_err, distB_err, U, AU_err, BU_err, I, AI_err, BI_err, &Rok_p1, &Rok_err_p1);
  EXPECT_EQ(1, 1);
  EXPECT_TRUE(true);
}
