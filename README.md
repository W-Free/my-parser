# my-parser
Parser de un lenguaje basado en javascript.

Dado el código de ejemplo ejemplo.txt

```
let s       string ;let  a int;
let bb boolean ;

bb= 
9 < (a - 333);

function gett void (string s, int gett)	
{
	put(s);
	let    _s boolean;
	put "hola";
	put a -3;
	put gett;	get s;
	return ;
	put (013 - (8 -3)-a);
}

let  _s string;

function RR int (int n, boolean b)
{
	let kk string ;
	let	log	boolean;
	log= n < 5 ;
	get kk;
	if (b && log)	return 9;
	return n - RR (n - 1, bb);	
}


_s = "Helloooooooooooooooooooooooooooooooooooooooooooooooooooooooooo";



function  cadena string (void)
{
	if (a < 000091 && bb) 
	{gett ("{9}", RR(0, bb));
		let			xx	int;
		xx=9;
		if (bb) return _s;
	}
	else      
		{return "";}}


	a= RR (a, bb);
	gett ("",99);
	 cadena();
```

El resultado de FicheroToken.txt es

```<102, > //es un let
<200, 1> //es un identificador con posicion en la tabla de simbolos: 1
<012, > //es un string
<203, > //es un ;
<102, > //es un let
<200, 2> //es un identificador con posicion en la tabla de simbolos: 2
<010, > //es un int
<203, > //es un ;
<102, > //es un let
<200, 3> //es un identificador con posicion en la tabla de simbolos: 3
<011, > //es un boolean
<203, > //es un ;
<200, 3> //es un identificador con posicion en la tabla de simbolos: 3
<201, > //es un =
<402, 9> //es una constante con valor 9
<302, > //es un <
<204, > //es un (
<200, 2> //es un identificador con posicion en la tabla de simbolos: 2
<301, > //es un -
<402, 333> //es una constante con valor 333
<205, > //es un )
<203, > //es un ;
<101, > //es un function
<200, 4> //es un identificador con posicion en la tabla de simbolos: 4
<013, > //es un void
<204, > //es un (
<012, > //es un string
<200, -1> //es un identificador con posicion en la tabla de simbolos: 1
<202, > //es un ,
<010, > //es un int
<200, -2> //es un identificador con posicion en la tabla de simbolos: 2
<205, > //es un )
<206, > //es un {
<104, > //es un put
<204, > //es un (
<200, -1> //es un identificador con posicion en la tabla de simbolos: 1
<205, > //es un )
<203, > //es un ;
<102, > //es un let
<200, -3> //es un identificador con posicion en la tabla de simbolos: 3
<011, > //es un boolean
<203, > //es un ;
<104, > //es un put
<401, hola> //es una cadena
<203, > //es un ;
<104, > //es un put
<200, 2> //es un identificador con posicion en la tabla de simbolos: 2
<301, > //es un -
<402, 3> //es una constante con valor 3
<203, > //es un ;
<104, > //es un put
<200, -2> //es un identificador con posicion en la tabla de simbolos: 2
<203, > //es un ;
<103, > //es un get
<200, -1> //es un identificador con posicion en la tabla de simbolos: 1
<203, > //es un ;
<107, > //es un return
<203, > //es un ;
<104, > //es un put
<204, > //es un (
<402, 13> //es una constante con valor 13
<301, > //es un -
<204, > //es un (
<402, 8> //es una constante con valor 8
<301, > //es un -
<402, 3> //es una constante con valor 3
<205, > //es un )
<301, > //es un -
<200, 2> //es un identificador con posicion en la tabla de simbolos: 2
<205, > //es un )
<203, > //es un ;
<207, > //es un }
<102, > //es un let
<200, 5> //es un identificador con posicion en la tabla de simbolos: 5
<012, > //es un string
<203, > //es un ;
<101, > //es un function
<200, 6> //es un identificador con posicion en la tabla de simbolos: 6
<010, > //es un int
<204, > //es un (
<010, > //es un int
<200, -1> //es un identificador con posicion en la tabla de simbolos: 1
<202, > //es un ,
<011, > //es un boolean
<200, -2> //es un identificador con posicion en la tabla de simbolos: 2
<205, > //es un )
<206, > //es un {
<102, > //es un let
<200, -3> //es un identificador con posicion en la tabla de simbolos: 3
<012, > //es un string
<203, > //es un ;
<102, > //es un let
<200, -4> //es un identificador con posicion en la tabla de simbolos: 4
<011, > //es un boolean
<203, > //es un ;
<200, -4> //es un identificador con posicion en la tabla de simbolos: 4
<201, > //es un =
<200, -1> //es un identificador con posicion en la tabla de simbolos: 1
<302, > //es un <
<402, 5> //es una constante con valor 5
<203, > //es un ;
<103, > //es un get
<200, -3> //es un identificador con posicion en la tabla de simbolos: 3
<203, > //es un ;
<105, > //es un if
<204, > //es un (
<200, -2> //es un identificador con posicion en la tabla de simbolos: 2
<303, > //es un &&
<200, -4> //es un identificador con posicion en la tabla de simbolos: 4
<205, > //es un )
<107, > //es un return
<402, 9> //es una constante con valor 9
<203, > //es un ;
<107, > //es un return
<200, -1> //es un identificador con posicion en la tabla de simbolos: 1
<301, > //es un -
<200, 6> //es un identificador con posicion en la tabla de simbolos: 6
<204, > //es un (
<200, -1> //es un identificador con posicion en la tabla de simbolos: 1
<301, > //es un -
<402, 1> //es una constante con valor 1
<202, > //es un ,
<200, 3> //es un identificador con posicion en la tabla de simbolos: 3
<205, > //es un )
<203, > //es un ;
<207, > //es un }
<200, 5> //es un identificador con posicion en la tabla de simbolos: 5
<201, > //es un =
<401, Helloooooooooooooooooooooooooooooooooooooooooooooooooooooooooo> //es una cadena
<203, > //es un ;
<101, > //es un function
<200, 7> //es un identificador con posicion en la tabla de simbolos: 7
<012, > //es un string
<204, > //es un (
<013, > //es un void
<205, > //es un )
<206, > //es un {
<105, > //es un if
<204, > //es un (
<200, 2> //es un identificador con posicion en la tabla de simbolos: 2
<302, > //es un <
<402, 91> //es una constante con valor 91
<303, > //es un &&
<200, 3> //es un identificador con posicion en la tabla de simbolos: 3
<205, > //es un )
<206, > //es un {
<200, 4> //es un identificador con posicion en la tabla de simbolos: 4
<204, > //es un (
<401, {9}> //es una cadena
<202, > //es un ,
<200, 6> //es un identificador con posicion en la tabla de simbolos: 6
<204, > //es un (
<402, 0> //es una constante con valor 0
<202, > //es un ,
<200, 3> //es un identificador con posicion en la tabla de simbolos: 3
<205, > //es un )
<205, > //es un )
<203, > //es un ;
<102, > //es un let
<200, -1> //es un identificador con posicion en la tabla de simbolos: 1
<010, > //es un int
<203, > //es un ;
<200, -1> //es un identificador con posicion en la tabla de simbolos: 1
<201, > //es un =
<402, 9> //es una constante con valor 9
<203, > //es un ;
<105, > //es un if
<204, > //es un (
<200, 3> //es un identificador con posicion en la tabla de simbolos: 3
<205, > //es un )
<107, > //es un return
<200, 5> //es un identificador con posicion en la tabla de simbolos: 5
<203, > //es un ;
<207, > //es un }
<106, > //es un else
<206, > //es un {
<107, > //es un return
<401, > //es una cadena
<203, > //es un ;
<207, > //es un }
<207, > //es un }
<200, 2> //es un identificador con posicion en la tabla de simbolos: 2
<201, > //es un =
<200, 6> //es un identificador con posicion en la tabla de simbolos: 6
<204, > //es un (
<200, 2> //es un identificador con posicion en la tabla de simbolos: 2
<202, > //es un ,
<200, 3> //es un identificador con posicion en la tabla de simbolos: 3
<205, > //es un )
<203, > //es un ;
<200, 4> //es un identificador con posicion en la tabla de simbolos: 4
<204, > //es un (
<401, > //es una cadena
<202, > //es un ,
<402, 99> //es una constante con valor 99
<205, > //es un )
<203, > //es un ;
<200, 7> //es un identificador con posicion en la tabla de simbolos: 7
<204, > //es un (
<205, > //es un )
<203, > //es un ;
<666, EOF>
```

FicheroTS correspondiente a la tabla de símbolos es


```
CONTENIDOS DE LA TABLA PRINCIPAL # 1 :
* LEXEMA : 's'
  + tipo : 'Cadena'
  + despl : '0'
* LEXEMA : 'a'
  + tipo : 'Entero'
  + despl : '64'
* LEXEMA : 'bb'
  + tipo : 'Boleano'
  + despl : '65'
* LEXEMA : 'gett'
  + tipo : 'Funcion'
  + EtiquetaFuncion : 'Etgett0'
  + TipoRetorno : 'Vacio'
  + numParametros : '2'
  + TipoParam1 : 'Cadena'
  + ModoParam1 : 'PorValor'
  + TipoParam2 : 'Entero'
  + ModoParam2 : 'PorValor'
* LEXEMA : '_s'
  + tipo : 'Cadena'
  + despl : '66'
* LEXEMA : 'RR'
  + tipo : 'Funcion'
  + EtiquetaFuncion : 'EtRR1'
  + TipoRetorno : 'Entero'
  + numParametros : '2'
  + TipoParam1 : 'Entero'
  + ModoParam1 : 'PorValor'
  + TipoParam2 : 'Boleano'
  + ModoParam2 : 'PorValor'
* LEXEMA : 'cadena'
  + tipo : 'Funcion'
  + EtiquetaFuncion : 'Etcadena2'
  + TipoRetorno : 'Cadena'
  + numParametros : '0'
CONTENIDOS DE LA TABLA SECUNDARIA # 1 :
* LEXEMA : 's'
  + tipo : 'Cadena'
  + despl : '1'
* LEXEMA : 'gett'
  + tipo : 'Entero'
  + despl : '0'
* LEXEMA : '_s'
  + tipo : 'Boleano'
  + despl : '65'
CONTENIDOS DE LA TABLA SECUNDARIA # 2 :
* LEXEMA : 'n'
  + tipo : 'Entero'
  + despl : '1'
* LEXEMA : 'b'
  + tipo : 'Boleano'
  + despl : '0'
* LEXEMA : 'kk'
  + tipo : 'Cadena'
  + despl : '2'
* LEXEMA : 'log'
  + tipo : 'Boleano'
  + despl : '66'
CONTENIDOS DE LA TABLA SECUNDARIA # 3 :
* LEXEMA : 'xx'
  + tipo : 'Entero'
  + despl : '0'


```


FicheroParse.txt es 

```
47	33	36	13	48	33	36	13	49	33	36	13	29	26	24	52	27	26	29	25	24	22	28	26	23	22	15	14	35	7	47	48	40	39	37	6	52	27	26	24	22	28	26	24	22	17	14	49	33	36	13	30	26	24	22	17	14	52	27	26	29	25	24	22	17	14	52	27	26	24	22	17	14	18	14	46	19	14	29	26	29	26	29	25	24	22	28	25	52	27	25	24	22	28	26	24	22	17	14	51	50	50	50	50	50	50	50	50	5	47	33	36	13	48	34	7	48	49	40	39	37	6	47	33	36	13	49	33	36	13	52	27	26	24	29	26	23	22	15	14	18	14	52	27	26	24	22	52	27	26	24	21	29	26	24	22	45	19	10	8	52	27	26	52	27	26	29	25	24	22	52	27	26	24	22	44	43	41	31	27	25	24	22	45	19	14	51	50	50	50	50	50	50	5	30	26	24	22	15	14	47	34	7	38	6	52	27	26	24	29	26	23	22	52	27	26	24	21	30	26	24	22	29	26	24	22	52	27	26	24	22	44	43	41	31	27	26	24	22	44	43	41	20	14	48	33	36	13	29	26	24	22	15	14	52	27	26	24	22	52	27	26	24	22	45	19	10	8	51	50	50	50	50	30	26	24	22	45	19	14	51	50	11	9	8	51	50	5	52	27	26	24	22	52	27	26	24	22	44	43	41	31	27	26	24	22	15	14	30	26	24	22	29	26	24	22	44	43	41	20	14	42	20	14	4	2	2	2	3	2	3	2	3	2	2	2	2	1

```

FicheroErrores.txt es vacío al no haber errores.
