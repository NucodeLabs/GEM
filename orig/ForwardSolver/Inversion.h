#pragma once

#include "stdint.h"

#define NOERROR 0
#define INCCORECT_ARG -137

//! Построение стартовой модели
int32_t StartModelSimple(int32_t* Nlay, double *Rom,  double *Hm,
	const int32_t Nraz,const double *AB2,const double *exp_roker,const double *exp_roker_err);

/*void InversionVes(const int32_t Nlay,double *Rom, int8_t* fix_r, double *Hm,const int8_t* fix_h,const int8_t* fix_z,
	const int32_t Nraz,const double *exp_roker,const double *exp_roker_err);*/