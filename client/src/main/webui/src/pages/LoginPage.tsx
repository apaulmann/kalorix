import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { toast } from "sonner";

interface LoginPageProps {
    onLogin: (token: string) => void;
}

export function LoginPage({ onLogin }: LoginPageProps) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const serverUrl = import.meta.env.VITE_SERVER;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            console.log("Server URL:", serverUrl);
            const response = await fetch(serverUrl + "/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password }),
            });
            if (!response.ok) {
                // Fehlerantwort auslesen und anzeigen
                const errorData = await response.json();
                throw new Error(errorData.message || "Login fehlgeschlagen");
            }
            const data = await response.json();
            onLogin(data.token);
            toast.success("Erfolgreich eingeloggt");
        } catch (error: any) {
            toast.error(error.message || "Login fehlgeschlagen");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center">
            <Card className="w-full max-w-md p-4">
                <CardContent>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <Input
                            type="text"
                            placeholder="Benutzername"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                        <Input
                            type="password"
                            placeholder="Passwort"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <Button type="submit" className="w-full" disabled={loading}>
                            {loading ? "Einloggen..." : "Einloggen"}
                        </Button>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}
