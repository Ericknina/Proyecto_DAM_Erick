package com.example.themethod.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class EjercicioConDetalles(
    val idEjercicio: Int,
    val nombre: String,
    val grupoMuscular: String,
    val series: Int,
    val repeticiones: Int,
    val pesoKgs: Double

)


@Dao
interface RutinaDao{
    // Guardar la rutina nueva en la base de datos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutina(rutina: Rutina): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarListaRutinas(rutinas: List<Rutina>)

    // Lee las Rutinas
    @Query("SELECT * FROM tabla_rutinas")
    fun ObtenerRutinas(): Flow<List<Rutina>>

    //Une el Catálogo de Ejercicios (e) con la Tabla Puente (re) buscando por el ID de la Rutina
    @Query("""
        SELECT e.idEjercicio, e.nombre,  e.grupoMuscular, re.series, re.repeticiones, re.pesoKgs 
        FROM tabla_ejercicio e 
        INNER JOIN tabla_rutina_ejercicio re ON e.idEjercicio = re.idEjercicio 
        WHERE re.idRutina = :rutinaId
    """)
    fun  obtenerEjerciciosDeRutina(rutinaId: Int): Flow<List<EjercicioConDetalles>>

    // 3. GUARDAR UN EJERCICIO EN LA RUTINA
    // Esta función guarda las series, repes y peso
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertRutinaEjercicio(crossRef: RutinaEjercicioCrossRef)


    @Delete
    suspend fun eliminarRutinaEjercicio(relacion: RutinaEjercicioCrossRef)


    // 1. Borramos las (relaciones) de esa rutina
    @Query("DELETE FROM tabla_rutina_ejercicio WHERE idRutina = :rutinaId")
    suspend fun limpiarEjerciciosDeRutina(rutinaId:  Int)

    // 2. Borramos la rutina en si
    @Query("DELETE FROM tabla_rutinas WHERE idRutina = :rutinaId")
    suspend fun eliminarRutinaPorId(rutinaId: Int)

    // 3. Limpieza de sesion
    @Query("DELETE FROM tabla_rutinas")
    suspend fun borrarTodasLasRutinas()

    @Query("DELETE FROM tabla_rutina_ejercicio")
    suspend fun borrarTodasLasRelaciones()

    // Dao para el historial
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarHistorial(historial: HistorialEntrenamiento)

    @Query("SELECT * FROM tabla_historial ORDER BY id DESC")
    fun obtenerTodoElHistorial(): Flow<List<HistorialEntrenamiento>>

}