#include <vector>
#include <algorithm>

#include "Inversion.h"

double median(std::vector<double> v)
{
	size_t n = v.size() / 2;
	std::nth_element(v.begin(), v.begin() + n, v.end());
	return v[n];
}

int32_t StartModelSimple(int32_t* Nlay, double *Rom, double *Hm,
	const int32_t Nraz,const double *AB,const  double *exp_roker,const double *exp_roker_err){
	const double criteria = 0.4; //!  ритерий по которому определ€ем различие сопроитвлени€ 
	const uint8_t N_count = 6;
	//! ѕроверка что мало измерений
	if (Nraz <= 0) {
		return INCCORECT_ARG;
	}
	//! ѕроверка на сортированность массива 
	std::vector<double> vAB(AB, AB + Nraz);
	if (!std::is_sorted(vAB.cbegin(), vAB.cend())) {
		return INCCORECT_ARG;
	}
	//! ≈сли одно измерение, то строим однородную среду
	if (Nraz == 1) {
		*Nlay = 1;
		Rom[0] = exp_roker[0];
		return NOERROR;
	}
	//! ≈сли измерений мало, строим двухслойную модель
	if (Nraz < N_count) {
		*Nlay = 2;
		const int left_count = Nraz / 2;
		double median_left = median(std::vector<double>(exp_roker, exp_roker+ left_count));
		double median_right = median(std::vector<double>(exp_roker+ left_count, exp_roker + Nraz));
		if (std::abs(median_left / median_right) < 1 - criteria) {
			*Nlay = 1;
			*Rom = (median_left+ median_right)/2;
			return NOERROR;
		}
		*Nlay = 2;
		Rom[0] = median_left;
		Rom[1] = median_right;
		Hm[0] = vAB[left_count] / 4;
		return NOERROR;
	}
	std::vector<double> med(N_count);
	std::vector<int> idx(N_count);
	for (int i = 0; i < N_count; i++) {
		med[i] = median(std::vector<double>(exp_roker + Nraz*i/ N_count, exp_roker + Nraz*(i+1) / N_count));
	}
	int idx_layer = 0;
	for (int i = 0; i < N_count-1;i++) {
		if (std::abs(med[i] / med[i+1]) > 1 - criteria) {
			Rom[idx_layer] = med[i];
			Hm[idx_layer]= vAB[Nraz*(i+1)/ N_count] / 4;
			idx_layer++;
		}
	}
	Rom[idx_layer] = med[med.size() - 1];
	idx_layer++;
	*Nlay = idx_layer;

	for (int i = *Nlay-2; i>0; i--) {
		Hm[i] = Hm[i] - Hm[i - 1];
	}
	return NOERROR;
}