# AndinaSalud

Aplicación multiplataforma para consultar, solicitar, cancelar y reprogramar citas de una red de centros médicos de Lima Este. El proyecto usa Kotlin Multiplatform y Compose Multiplatform. En esta versión los datos permanecen en memoria: no se usa una API ni una base de datos.

**Autor:** Roberto Samuel Valencia Saavedra · **Código:** 202410319  
**Curso:** Desarrollo de Aplicaciones Móviles · **Docente:** Benjamín David Reyna Barreto

## Funcionalidades

| Código | Implementación |
| --- | --- |
| RF-01 | Inicio con saludo, próxima cita y accesos rápidos. |
| RF-02 | Lista ordenada y filtros por estado. |
| RF-03 | Detalle con indicaciones y cancelación confirmada. |
| RF-04 | Solicitud con errores por campo. |
| RF-05 | Búsqueda por especialidad o médico sin distinguir tildes ni mayúsculas. |
| RF-06 | Perfil y tema claro u oscuro. |
| RF-07 | Navegación inferior y retorno con Atrás. |
| RF-08 | Estados de carga, contenido, vacío y error. |

La Parte II incorpora cuatro cambios en commits independientes: SC-A (chip «Hoy» combinado con estado), SC-B (contador de programadas y bloqueo al llegar al cupo), SC-C (Presencial o Teleconsulta) y SC-D (reprogramación con validación compartida). El horario clínico se interpreta en la zona `America/Lima`, incluso si el emulador está en GMT.

## Organización del código

El módulo `shared/src/commonMain/kotlin/pe/upeu/andinasalud` contiene:

- `domain/model`: entidades y estados de cita.
- `domain/repository`: contrato `CitaRepository`, independiente del origen de datos.
- `domain/usecase`: reglas RN-01 a RN-05, cupo, cancelación y reprogramación.
- `data/local`: paciente, sedes, especialidades, médicos y citas de ejemplo.
- `data/repository`: implementación en memoria de `CitaRepository`.
- `presentation`: pantallas Compose, ViewModels, `StateFlow` y estados de interfaz.
- `di`: configuración de Koin.

Las pantallas no acceden directamente a la fuente simulada. Para incorporar una API en una versión futura se implementaría el contrato del repositorio en `data` y se sustituiría su registro en Koin. La versión evaluada no incorpora dependencias de red ni persistencia.

## Ejecución y pruebas

Abrir la carpeta del proyecto en Android Studio, sincronizar Gradle, seleccionar `androidApp` y ejecutar en un emulador o teléfono Android. Para generar el APK y ejecutar las pruebas de dominio en Windows:

```powershell
.\gradlew.bat :androidApp:assembleDebug :shared:testAndroidHostTest
```

El código compartido se verificó con `:shared:compileKotlinIosSimulatorArm64`. Eso comprueba compilación del módulo, **no ejecución de la app en iOS**; esta última requiere macOS con Xcode o un dispositivo iOS compatible.

Los datos se reinician al cerrar el proceso, porque el repositorio es solo en memoria. Al iniciar hay tres citas Programadas, por lo que «Solicitar cita» queda deshabilitado hasta cancelar una de ellas. Solo se acepta una fecha y hora futuras según Lima. Para probar «Hoy» con contenido, se puede reprogramar una cita Programada a una hora posterior del día actual. La cancelación exige más de 24 horas de anticipación.

## Git y evidencias

El trabajo está en `feature/citas-valencia`. La rama `develop` contiene la base integrada y `main` no se ha actualizado con esta funcionalidad. Los cambios SC-A, SC-B, SC-C y SC-D están en commits separados. Las capturas finales y la demostración en iOS deben verificarse antes de etiquetar una versión de entrega.
