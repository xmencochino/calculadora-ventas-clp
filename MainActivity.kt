package com.example.calculadoraventas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculadoraVentasScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculadoraVentasScreen() {
    var costoTexto by remember { mutableStateOf("") }
    var porcentajeGanancia by remember { mutableFloatStateOf(30f) } // Porcentaje inicial del 30%

    // Lógica de cálculo en Pesos Chilenos (redondeado a enteros)
    val costoBase = costoTexto.toDoubleOrNull() ?: 0.0
    val gananciaMonto = costoBase * (porcentajeGanancia / 100)
    val precioSubtotal = costoBase + gananciaMonto
    val ivaMonto = precioSubtotal * 0.19 // IVA del 19% en Chile
    val precioFinal = precioSubtotal + ivaMonto

    // Formateador para pesos chilenos (ej: $ 1.500.000)
    val formatoCLP = NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
        maximumFractionDigits = 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Calculadora de Ventas (CLP)",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Campo para ingresar el costo base del producto
        OutlinedTextField(
            value = costoTexto,
            onValueChange = { nuevoTexto ->
                // Solo permite dígitos
                if (nuevoTexto.all { it.isDigit() }) {
                    costoTexto = nuevoTexto
                }
            },
            label = { Text("Costo base del producto (CLP)") },
            placeholder = { Text("Ej: 10000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Control deslizante para el porcentaje de ganancia (10% al 200%)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Margen de ganancia: ${porcentajeGanancia.roundToLong()}%",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Slider(
                    value = porcentajeGanancia,
                    onValueChange = { porcentajeGanancia = it },
                    valueRange = 10f..200f,
                    steps = 189
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Desglose detallado de la venta
        Text(
            text = "Desglose de la Venta",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        FilaResultado(label = "Costo base:", valorFormatted = formatoCLP.format(costoBase))
        FilaResultado(label = "Ganancia (${porcentajeGanancia.roundToLong()}%):", valorFormatted = formatoCLP.format(gananciaMonto))
        FilaResultado(label = "Subtotal (Neto):", valorFormatted = formatoCLP.format(precioSubtotal))
        FilaResultado(label = "IVA (19%):", valorFormatted = formatoCLP.format(ivaMonto))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Precio Final de Venta:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatoCLP.format(precioFinal),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun FilaResultado(label: String, valorFormatted: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = valorFormatted,
            fontWeight = FontWeight.Medium
        )
    }
}

