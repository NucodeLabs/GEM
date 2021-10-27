#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include "VES.H"

int long_of_row;  /* Длина насчитанного ряда пространственных частот */
int Nk[ NRF];     /* Массив индексов интерполяции */
_FPREC A[MAXEXPERIMENT*NPOLINOM];
                  /* Массив коэффициентов интерполяции */

/*                                                                        */
/*  Invser - расчет коэффициентов установки по Er                         */
/*  Возвращает 1 при превышении допустимого диапазона разносов            */
/*     В случае чего - увеличить NRF !!!                                  */
/*                                                                        */
int Invser( _FPREC *Raz, int NRaz )
{
_FPREC rf[ NRF];         /* Набор пространственных частот начиная с Raz1 */
_FPREC daup;
_FPREC *xx;
int   i,j,k,m;

/* насчитывается массив RF - разносов соответствующих процедуре */

  
/* определение точек интерполяции */
	rf[0] = Raz[0];
	for( i=1; i<NRF; i++){
		if( (rf[i-1] > Raz[NRaz-1]) && (i>=NPOLINOM)) break;
		rf[i] = FR * rf[i-1];
		}/*for*/
	if(i>=NRF) return 1;
	long_of_row = i;

/*  определяем оптимальные узлы из rf для каждого разноса из Raz */
	for( j=i=0; i<NRaz; i++){
		while( Raz[i] >= rf[j]) j++;
		j -= (NPOLINOM/2);
		if(j<0) j=0;
		if( (j+NPOLINOM)>long_of_row) j = long_of_row-NPOLINOM;
		Nk[i]=j;
		}/*for*/

/* определение коэффициентов интерполяции по Лагранжу */
	for( j=i=0; i<NRaz; i++){
		xx = rf + Nk[i];
		for( k=0; k<NPOLINOM; k++){
			for( m=0,daup=1.; m<NPOLINOM; m++){
				if(k==m) continue;
				daup *= (Raz[i]-xx[m])/(xx[k]-xx[m]);
				}/*for*/
			A[j++] = daup;
			}/*for*/
		}/*for*/
return 0;		
}

/*                                                                       */
/*    Veser - процедура расчета прямой задачи ВЭЗ                        */
/*                                                                       */
void Ves( _FPREC *Rom, _FPREC *Hm, int Nlay, _FPREC *Raz,
				 int Nraz, _FPREC *Roker)
/* _FPREC *Rom;      Удельное сопротивление */
/* _FPREC *Hm;       Мощность */
/* _FPREC *Roker;    Кажущееся сопротивление */
/* _FPREC *Raz;      Разносы */
/* int   Nlay, Nraz;  */
{
int l, m, j, i, ke;
_FPREC *rokfl, *trok, *t;
_FPREC a1, a11, a2, b, *u;
_FPREC y = Raz[0]/822.8;
int Nlay1 = Nlay-1;

  if( Nraz > MAXEXPERIMENT ){
     puts("\nVES:\nMaximal number of distances is exseeded.");
     return;
  }   

  if( Invser( Raz, Nraz ) == 1){
    puts("\nVes:\nExseeding of raznos range.");
    return;
  }

  rokfl = (_FPREC *)malloc( sizeof(_FPREC)*NRF );
  u     = (_FPREC *)malloc(sizeof(_FPREC)*(MAXLAYER-1));
  trok  = (_FPREC *)malloc( sizeof(_FPREC)*(NRF+35) );

  if( rokfl==NULL || u==NULL || trok==NULL){
    puts("\nVes:\nCan't allocate memory.");
    return;
  }

  /* Цикл по всем частотам фильтра */
	for( j=0; j<long_of_row+35; j++){
		b  = Rom[Nlay1];
		ke = Nlay1-1;
	  /* уменьшение по возможности числа расчета экспонент */
		for( i=0; i<=ke; i++)
			{ u[i] = Hm[i]/y;
			  if((5.-u[i]) <= 0. )
				  { b = Rom[i]; ke=i-1; break;}
			}
		/* Расчет слоистой функции */
		for(i=ke; i>=0; i--){
			a1  = exp( u[i] );
			a11 = 1. / a1;
			a2  = ( a1 - a11 ) / (a1 + a11 );
			b   = ( b + a2 * Rom[i]) / ( 1. + a2 * b / Rom[i]);
		}/*for*/
		trok[j] = b;
		y *= FR;
	}/* for по частотам */

	for( m=0; m<long_of_row; m++){
		t = trok + m;  /* указатель! */
		rokfl[m] = (42.0*t[0]-103.0*t[2]+144.0*t[4]-211.0*t[6]
				  +330.0*t[8]-574.0*t[10]+1184.0*t[12]
				  -3162.0*t[14]+10219.0*t[16]-24514.0*t[18]
				  +18192.0*t[20]+6486.0*t[22]+1739.0*t[24]
				  +79.0*t[26]+200.0*t[28]-106.0*t[30]
				  +93.0*t[32]-38.0*t[34]) * 1.e-4;
		}/*for*/

	/* Интерполяция в реальные разносы */
	for( m=i=0; i<Nraz; i++){
		a1 = 0.;
		l = Nk[i];
		for( j=0; j<NPOLINOM; j++) a1 += A[m++]*rokfl[l++];
		Roker[i] = a1;
  }/* for*/

  free( rokfl );
  free( u );
  free( trok );

}
