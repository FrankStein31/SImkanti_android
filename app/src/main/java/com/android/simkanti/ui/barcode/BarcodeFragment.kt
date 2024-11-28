package com.android.simkanti.ui.barcode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.simkanti.databinding.FragmentBarcodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import java.util.Collections

class BarcodeFragment : Fragment() {
    private var _binding: FragmentBarcodeBinding? = null
    private val binding get() = _binding!!
    private lateinit var barcodeViewModel: BarcodeViewModel
    private lateinit var barcodeScanner: DecoratedBarcodeView

    private val barcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            result?.let { barcodeResult ->
                // Extract and validate QR code content (assuming format is "amount:value")
                val content = barcodeResult.text
                try {
                    val parts = content.split(":")
                    if (parts.size == 2 && parts[0] == "amount") {
                        val amount = parts[1].toDoubleOrNull()
                        amount?.let {
                            binding.textQrAmount.text = "Rp ${String.format("%,.2f", it)}"
                            barcodeScanner.pause()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error reading QR Code", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarcodeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize ViewModel
        barcodeViewModel = ViewModelProvider(this).get(BarcodeViewModel::class.java)

        // Setup barcode scanner
        barcodeScanner = binding.zxingBarcodeScanner
        barcodeScanner.decoderFactory = DefaultDecoderFactory(
            Collections.singletonList(BarcodeFormat.QR_CODE)
        )
        barcodeScanner.setStatusText("Scan QR Code")

        // Setup pay button
        binding.btnPay.setOnClickListener {
            Toast.makeText(requireContext(), "Pembayaran akan diproses", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if (checkCameraPermission()) {
            barcodeScanner.resume()
            barcodeScanner.decodeContinuous(barcodeCallback)
        } else {
            requestCameraPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeScanner.pause()
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}