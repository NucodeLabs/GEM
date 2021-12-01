#define _FPREC double         /* Определение плавающего типа        */

#ifndef PI
#define PI      3.14159       /* Определение значения PI            */
#endif

#define MAXLAYER      50      /* Предельное число слоев в модели    */
#define MAXEXPERIMENT 40      /* Предельное число полевых измерений */

#define FR 1.335214
#define NRF 40
#define NPOLINOM 5

/* Rom        Удельное сопротивление  */
/* Hm         Мощность                */
/* Nlay                               */
/* Raz        Разносы                 */
/* Nraz                               */
/* Roker      Кажущееся сопротивление */

void Ves( _FPREC *Rom, _FPREC *Hm, int Nlay, _FPREC *Raz, int Nraz,
          _FPREC *Roker);
