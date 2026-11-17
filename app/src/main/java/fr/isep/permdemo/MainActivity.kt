package fr.isep.permdemo

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.isep.permdemo.permissions.DemoCatalog
import fr.isep.permdemo.ui.detail.PermissionDetailScreen
import fr.isep.permdemo.ui.home.HomeScreen
import fr.isep.permdemo.ui.theme.PermDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lightBars = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = lightBars, navigationBarStyle = lightBars)
        setContent {
            PermDemoTheme {
                PermDemoNavHost()
            }
        }
    }
}

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{caseId}"
    fun detail(caseId: String) = "detail/$caseId"
}

@Composable
fun PermDemoNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onOpenCase = { navController.navigate(Routes.detail(it.id)) },
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("caseId") { type = NavType.StringType }),
        ) { entry ->
            val case = DemoCatalog.byId(entry.arguments?.getString("caseId").orEmpty())
            PermissionDetailScreen(
                case = case,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
