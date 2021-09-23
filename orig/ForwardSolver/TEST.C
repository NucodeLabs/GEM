#include <stdio.h>
#include <math.h>

#include "VES.H"

#define Nlay 3    /* Number of layers    */
#define Nraz 40    /* Number of distances */

_FPREC Raz[Nraz]; /* Distances           */
_FPREC Ro[Nlay];  /* Layers resistivites */
_FPREC Th[Nlay];  /* Layers thicknesses  */
_FPREC RoK[Nraz]; /* Apparent resistivity*/

int main(void){
int i;
double sr;

  sr = 4./(double)(Nraz-1);
  printf("%le\n", sr );
  
  /* Generating distances */
  for( i=0; i<Nraz; i++ ) Raz[i] = pow(10., i*sr);
  Raz[0] = .2;
  
  /* Generateing thicknesses & resistivites */
  Th[0] = 1., Ro[0] = 1000.;
  Ro[1] = 100., Ro[2] = 1.;
  for(i=1; i<Nlay; i++ ){ 
    Th[i] = Th[i-1]+2.;
//    Ro[i] = Ro[i-1]/pow(10.,i);
  }
  Th[Nlay-1] = 1.e+10;
  
  Ves( Ro, Th, Nlay, Raz, Nraz, RoK );
  
  printf( "MODEL:\n" );
  for(i=0;i<Nlay; i++)
    printf("H=%+.3le Ro=%+.3le\n", Th[i], Ro[i]);
    
  for(i=0; i<Nraz; i++ )
    printf("%+.3le %+.3le\n", Raz[i]/2., RoK[i] );

return 1;
}
