import { useEffect, useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { ThemeProvider } from "@/components/theme-provider";
import { LoginPage } from "@/pages/LoginPage";
import { PersonsOverview } from "@/pages/PersonsOverview";
import { TrackingView } from "@/pages/TrackingView"; // Importieren
import { Toaster } from "@/components/ui/sonner";

// Typ für die dekodierten JWT-Daten definieren
type DecodedToken = {
    upn: string;
    groups: string[]; // Roles
    exp: number;
};

export default function App() {
    const [token, setToken] = useState<string | null>(null);
    const [user, setUser] = useState<DecodedToken | null>(null);
    const [isLoading, setIsLoading] = useState(true); // Ladezustand hinzufügen

    useEffect(() => {
        const storedToken = localStorage.getItem("auth_token");
        if (storedToken) {
            try {
                const decodedUser = jwtDecode<DecodedToken>(storedToken);
                // Prüfen, ob das Token abgelaufen ist
                if (decodedUser.exp * 1000 > Date.now()) {
                    setToken(storedToken);
                    setUser(decodedUser);
                } else {
                    // Token ist abgelaufen, also entfernen
                    localStorage.removeItem("auth_token");
                }
            } catch (error) {
                console.error("Fehler beim Dekodieren des Tokens:", error);
                localStorage.removeItem("auth_token");
            }
        }
        setIsLoading(false); // Ladevorgang abschließen
    }, []);

    const handleLogin = (newToken: string) => {
        const decodedUser = jwtDecode<DecodedToken>(newToken);
        localStorage.setItem("auth_token", newToken);
        setToken(newToken);
        setUser(decodedUser);
    };

    const handleLogout = () => {
        localStorage.removeItem("auth_token");
        setToken(null);
        setUser(null);
    };

    // Während der Token-Prüfung einen Ladebildschirm anzeigen
    if (isLoading) {
        return <div>Wird geladen...</div>; // Oder eine schicke Lade-Komponente
    }

    // Funktion zur Bestimmung der Startroute basierend auf der Rolle
    const getHomeRoute = () => {
        if (!user) return "/";
        if (user.groups.includes("admin")) return "/persons";
        if (user.groups.includes("user")) return "/tracking";
        return "/"; // Fallback
    };

    return (
        <ThemeProvider defaultTheme="dark">
            <Router>
                <Routes>
                    {!token ? (
                        <Route path="/*" element={<LoginPage onLogin={handleLogin} />} />
                    ) : (
                        <>
                            {/* Standardroute basierend auf Rolle */}
                            <Route path="/" element={<Navigate to={getHomeRoute()} replace />} />

                            {/* Routen für Admins */}
                            {user?.groups.includes("admin") && (
                                <>
                                    <Route path="/persons" element={<PersonsOverview token={token} onLogout={handleLogout} />} />
                                </>
                            )}

                            {/* Routen für User */}
                            {user?.groups.includes("user") && (
                                <Route path="/tracking" element={<TrackingView onLogout={handleLogout} user={user?.upn} token={token} />} />
                            )}

                            {/* Fallback, falls eine ungültige URL aufgerufen wird */}
                            <Route path="*" element={<Navigate to={getHomeRoute()} replace />} />
                        </>
                    )}
                </Routes>
                <Toaster />
            </Router>
        </ThemeProvider>
    );
}