//
//  ComposeView.swift
//  PrintSandboxIOS
//
//  Created by Vitalii Andrusyshyn on 17/07/2026.
//

import SwiftUI
import PrintSandbox

struct ComposeView: UIViewControllerRepresentable {

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(
        _ uiViewController: UIViewController,
        context: Context
    ) {
    }
}
