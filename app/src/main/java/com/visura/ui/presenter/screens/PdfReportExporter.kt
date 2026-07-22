package com.visura.ui.presenter

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.visura.ui.viewmodels.CompletedInspection

object PdfReportExporter {

    fun exportAndPrint(context: Context, inspection: CompletedInspection) {
        val htmlContent = generateHtml(inspection)

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                val printAdapter = webView.createPrintDocumentAdapter("Laudo_${inspection.ownerName}")
                printManager.print(
                    "Laudo_Vistoria_${inspection.ownerName}",
                    printAdapter,
                    PrintAttributes.Builder().build()
                )
            }
        }

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    private fun generateHtml(inspection: CompletedInspection): String {
        val roomsHtml = inspection.rooms.joinToString("") { room ->
            val itemsHtml = if (room.items.isEmpty()) {
                "<tr><td colspan='3' style='padding: 10px; color: #888;'>Nenhum item cadastrado neste cômodo.</td></tr>"
            } else {
                room.items.joinToString("") { item ->
                    val badgeColor = when (item.condition.lowercase()) {
                        "novo", "bom" -> "#2e7d32"
                        "regular" -> "#f57c00"
                        else -> "#c62828"
                    }
                    """
                    <tr>
                        <td style="padding: 10px; border-bottom: 1px solid #eee;">${item.name}</td>
                        <td style="padding: 10px; border-bottom: 1px solid #eee;">
                            <span style="color: $badgeColor; font-weight: bold;">${item.condition}</span>
                        </td>
                        <td style="padding: 10px; border-bottom: 1px solid #eee; color: #555;">${item.observation.ifBlank { "Sem avarias / Ok" }}</td>
                    </tr>
                    """.trimIndent()
                }
            }

            """
            <div style="margin-bottom: 24px;">
                <h3 style="color: #1a237e; border-bottom: 2px solid #1a237e; padding-bottom: 4px; margin-bottom: 8px;">${room.roomName}</h3>
                <table style="width: 100%; border-collapse: collapse; font-size: 14px;">
                    <thead>
                        <tr style="background-color: #f5f5f5; text-align: left;">
                            <th style="padding: 8px; width: 35%;">Item</th>
                            <th style="padding: 8px; width: 25%;">Estado</th>
                            <th style="padding: 8px; width: 40%;">Observação / Avaria</th>
                        </tr>
                    </thead>
                    <tbody>
                        $itemsHtml
                    </tbody>
                </table>
            </div>
            """.trimIndent()
        }

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: sans-serif; padding: 20px; color: #333; }
                .header { text-align: center; margin-bottom: 24px; border-bottom: 2px solid #1a237e; padding-bottom: 10px; }
                .header h1 { color: #1a237e; margin: 0; font-size: 24px; }
                .header p { color: #666; margin-top: 4px; font-size: 14px; }
                .card { background: #f8f9fa; border: 1px solid #e0e0e0; border-radius: 8px; padding: 16px; margin-bottom: 24px; }
                .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; font-size: 14px; }
                .info-item { margin-bottom: 4px; }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>LAUDO DE VISTORIA IMOBILIÁRIA</h1>
                <p>Relatório Completo de Inspeção de Imóvel</p>
            </div>

            <div class="card">
                <div class="info-grid">
                    <div class="info-item"><strong>Proprietário:</strong> ${inspection.ownerName}</div>
                    <div class="info-item"><strong>Inquilino:</strong> ${inspection.tenantName.ifBlank { "Não informado" }}</div>
                    <div class="info-item"><strong>Vistoriador:</strong> ${inspection.inspectorName.ifBlank { "Não informado" }}</div>
                    <div class="info-item"><strong>Data:</strong> ${inspection.date}</div>
                </div>
                ${if (inspection.observations.isNotBlank()) "<p style='margin-top: 12px; font-size: 13px; color: #444;'><strong>Observações Gerais:</strong> ${inspection.observations}</p>" else ""}
            </div>

            <h2 style="font-size: 18px; color: #1a237e; margin-bottom: 16px;">Detalhamento dos Cômodos</h2>
            $roomsHtml
        </body>
        </html>
        """.trimIndent()
    }
}