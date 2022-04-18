#include "asa047.h"
#include <vector>
#include <list>

typedef struct {
	std::vector<double> init_start;
	std::vector<int> idx_convert;
	nelmin_fn init_fn;
	const void *init_data;
}nelmin_fn_fix_data;

double nelmin_fn_fix(const double x[], const void *pdata) {
	const auto data = (nelmin_fn_fix_data*)pdata;
	for (int i = 0; i < data->idx_convert.size(); i++) {
		data->init_start[data->idx_convert[i]] = x[i];
	}
	return (*data->init_fn)(data->init_start.data(),data->init_data);
}


void nelmin_fix(nelmin_fn fn, int n, double start[], int fix[], double xmin[],
	double *ynewlo, double reqmin, double step[], int konvge, int kcount,
	int *icount, int *numres, int *ifault, const void* pdata) {
	for (int i = 0; i < n; ++i) {
		if (fix[i] != UNFIX_PARAM && fix[i] != FIX_PARAM) {
			*ifault = INCORRECT_FIX_IND;
			return;
		}
	}
	std::list <int> lidxConverter;
	for (int i = 0; i < n; ++i) {
		if (fix[i] == UNFIX_PARAM) {
			lidxConverter.push_back(i);
		}
	}
	std::vector<int> idxConverter(lidxConverter.cbegin(), lidxConverter.cend());
	std::vector<double> new_xmin(idxConverter.size());
	std::vector<double> new_start(idxConverter.size());
	std::vector<double> new_step(idxConverter.size());
	for (int i = 0; i < idxConverter.size(); i++) {
		new_start[i] = start[idxConverter[i]];
		new_step[i] = step[idxConverter[i]];
	}
	const nelmin_fn_fix_data data{ std::vector<double>(start,start+n),idxConverter,fn,pdata };

	nelmin(nelmin_fn_fix, idxConverter.size(), new_start.data(), new_xmin.data(), ynewlo, reqmin, new_step.data(), konvge, kcount, icount, numres, ifault, &data);
	std::memcpy(xmin,start,n*sizeof(double));
	for (int i = 0; i < idxConverter.size(); i++) {
		start[idxConverter[i]] = new_start[i];
		xmin[idxConverter[i]] = new_xmin[i];
	}
}

double bSearch_nelmin_fn(const nelmin_fn fn,const void* pdata, const double target_fn, const double target_eps,
	const int n,const double xmin[],const int idx,const double start_min_val,const double start_max_val) {
	std::vector<double> cur_xmin(xmin, xmin+n);
	const double min_distance_val = 1e-6;
	double min_val= start_min_val, max_val= start_max_val;
	cur_xmin[idx] = min_val;
	double fn_min_val = fn(cur_xmin.data(), pdata);
	cur_xmin[idx] = max_val;
	double fn_max_val = fn(cur_xmin.data(), pdata);
	
	if ((fn_min_val < target_fn && fn_max_val < target_fn)|| (fn_min_val > target_fn && fn_max_val > target_fn)) {
		return NAN;
	}
	while (fabs(fn_max_val - fn_min_val) > target_eps&& fabs(max_val - min_val) > min_distance_val) {
		double center_val = (min_val + max_val) / 2;
		cur_xmin[idx] = center_val;
		double fn_center_val= fn(cur_xmin.data(), pdata);
		if ((fn_center_val - target_fn)*(fn_center_val - fn_min_val)>0) {
			fn_max_val = fn_center_val;
			max_val = center_val;
		}
		else {
			fn_min_val = fn_center_val;
			min_val = center_val;
		}

	}
	return (min_val + max_val) / 2;
}


void ResponseFunction(const nelmin_fn fn, const void* pdata, const int n, const double xmin[], const double eps, 
	const double xmin_border_init[], const double xmax_border_init[], double xmin_border[], double xmax_border[]) {
	const auto fn_min= fn(xmin, pdata);
	const auto fn_target = fn_min * (1 + eps);
	for (int i = 0; i < n; i++) {
		double cur_min_border= bSearch_nelmin_fn(fn, pdata, fn_target, fn_target*eps*0.01, n, xmin, i, xmin_border_init[i], xmin[i]);
		xmin_border[i] = std::isnan(cur_min_border)? xmin_border_init[i]: cur_min_border;
		double cur_max_border = bSearch_nelmin_fn(fn, pdata, fn_target, fn_target*eps*0.01, n, xmin, i, xmin[i], xmax_border_init[i]);
		xmax_border[i] = std::isnan(cur_max_border) ? xmax_border_init[i] : cur_max_border;
	}
}